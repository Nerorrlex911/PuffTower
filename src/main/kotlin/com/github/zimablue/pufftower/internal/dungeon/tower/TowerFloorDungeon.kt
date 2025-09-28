package com.github.zimablue.pufftower.internal.dungeon.tower

import com.github.zimablue.pufftower.api.team.Team
import com.github.zimablue.pufftower.internal.dungeon.SingleInstanceDungeon
import com.github.zimablue.pufftower.internal.dungeon.feature.Feature
import com.github.zimablue.pufftower.internal.manager.PTConfig
import net.hollowcube.polar.PolarLoader
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.instance.LightingChunk
import kotlin.time.Duration

/**
 * represents a single floor of the PuffTower game
 * @param floorConfig the configuration of this floor
 * @param towerGame the game of the tower this floor belongs to
 */
class TowerFloorDungeon(val floorConfig: FloorConfig, val towerGame: TowerGame): SingleInstanceDungeon() {

    override val team: Team = towerGame.team

    override fun instance(): Instance {
        val container = MinecraftServer.getInstanceManager().createInstanceContainer()
        container.chunkLoader = PolarLoader(floorConfig.mapPath)
        container.setChunkSupplier { instance,i,i2 ->
            LightingChunk(instance,i,i2)
        }
        return container
    }

    override fun spawnPosition(player: Player): Pos {
        return floorConfig.spawnPosition
    }

    override val features: List<Feature>
        get() = DEFAULT_FEATURES+floorConfig.features

    override suspend fun init() {
        super.init()
        // generates mobs
//        floorConfig.generators.forEach { generator ->
//            // generators should set the entity to proper position and instance
//            // we don't care about the result so no need to suspend
//            generator.generate(this)
//        }
        TODO()
    }

    override suspend fun onStart() {
        super.onStart()
        floorConfig.onStart.invoke(this)
    }

    override suspend fun onEnd() {
        floorConfig.onEnd.invoke(this)
        // update the lounge and teleport all players to it, wait for all teleports to finish
        team.setInstance(towerGame.loungeInstance,PTConfig.loungeSpawn)
        TODO("Not yet implemented")
    }

    override suspend  fun onShutdown(timeout: Duration) {
        floorConfig.onShutdown.invoke(this)
        TODO("save tower data?")
    }

    companion object {
        val DEFAULT_FEATURES: List<Feature> = listOf()
    }
}