package com.github.zimablue.pufftower.api.team

import kotlinx.coroutines.future.await
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import java.util.*
import java.util.concurrent.CompletableFuture

class Team(
    val captain: UUID,
    val memberUUIDs: MutableList<UUID>
) {
    val members: List<Player>
        get() = memberUUIDs.mapNotNull { MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(it) }

    suspend fun setInstance(instance: Instance,pos: Pos?=null) {
        val futures = members.map { member ->
            if (pos==null) member.setInstance(instance) else member.setInstance(instance,pos)
        }
        CompletableFuture.allOf(*futures.toTypedArray()).await()
    }
}