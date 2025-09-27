package com.github.zimablue.pufftower.internal.manager

import com.github.zimablue.pufftower.util.toPos
import net.hollowcube.polar.PolarLoader
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import taboolib.module.configuration.Configuration
import java.nio.file.Path
import java.util.*

object PTConfig {
    lateinit var config: Configuration
        private set
    val lobby: Instance
        get() = MinecraftServer.getInstanceManager().getInstance(UUID.fromString(config.getString("lobby.instance")!!))!!
    val lobbySpawn: Pos
        get() = config.getDoubleList("lobby.spawn").toPos()
    val loungeSpawn: Pos
        get() = config.getDoubleList("lounge.spawn").toPos()
    fun genLoungeInstance(): Instance {
        val instance = MinecraftServer.getInstanceManager().createInstanceContainer()
        instance.chunkLoader = PolarLoader(Path.of(config.getString("lounge.path")!!))
        instance.setChunkSupplier { instance, i, i2 ->
            net.minestom.server.instance.LightingChunk(instance, i, i2)
        }
        return instance
    }
}