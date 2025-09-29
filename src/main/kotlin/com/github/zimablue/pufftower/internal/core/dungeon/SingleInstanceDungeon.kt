package com.github.zimablue.pufftower.internal.core.dungeon

import com.github.zimablue.pufftower.api.dungeon.Dungeon
import com.github.zimablue.pufftower.api.dungeon.feature.Feature
import com.github.zimablue.pufftower.internal.manager.PTConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.event.instance.RemoveEntityFromInstanceEvent
import net.minestom.server.instance.Instance
import java.util.concurrent.CompletableFuture

abstract class SingleInstanceDungeon: Dungeon() {

    abstract fun instance(): Instance

    lateinit var instance: Instance
        private set

    abstract fun spawnPosition(player: Player) : Pos

    abstract val features: List<Feature>

    override suspend fun init() {
        withContext(Dispatchers.IO) {
            instance = instance()
        }
        instance.eventNode().addListener(RemoveEntityFromInstanceEvent::class.java) { event ->
            if (event.entity !is Player) return@addListener
            if (instance.players.size > 1) return@addListener
            // All players have left. We can remove this instance once the player is removed.
            MinecraftServer.getSchedulerManager().scheduleNextTick {
                MinecraftServer.getInstanceManager().unregisterInstance(instance)
            }
        }
        features.forEach { feature -> feature.hook(instance.eventNode()) }
    }

    override suspend fun onStart() {
        val futures = team.members.map { member ->
            member.setInstance(instance)
        }
        CompletableFuture.allOf(*futures.toTypedArray()).await()
    }

    override suspend fun kill() {
        // kick all players to the default place
        team.setInstance(PTConfig.lobby,PTConfig.lobbySpawn)
        super.kill()
    }


}