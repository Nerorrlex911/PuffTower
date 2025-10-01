package com.github.zimablue.pufftower

import com.github.zimablue.devoutserver.DevoutServer
import com.github.zimablue.devoutserver.plugin.Plugin
import com.github.zimablue.devoutserver.plugin.script.PluginScriptManager
import com.github.zimablue.pufftower.api.manager.DungeonManager
import com.github.zimablue.pufftower.api.manager.FeatureManager
import com.github.zimablue.pufftower.api.manager.ItemManager
import com.github.zimablue.pufftower.internal.core.command.PuffTowerCommand
import com.github.zimablue.pufftower.internal.manager.DungeonManagerImpl
import com.github.zimablue.pufftower.internal.manager.FeatureManagerImpl
import com.github.zimablue.pufftower.internal.manager.ItemManagerImpl
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.event.player.PlayerSpawnEvent
import net.minestom.server.instance.Instance
import net.minestom.server.instance.LightingChunk
import net.minestom.server.instance.anvil.AnvilLoader
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.generator.GenerationUnit
import net.minestom.server.utils.chunk.ChunkSupplier

object PuffTower : Plugin() {

    val puffTowerEventNode = EventNode.all("PuffTower").setPriority(15)

    val dungeonManager: DungeonManager = DungeonManagerImpl
    val featureManager: FeatureManager = FeatureManagerImpl
    val scriptManager: PluginScriptManager = PluginScriptManager(this)
    val itemManager: ItemManager = ItemManagerImpl

    lateinit var instance: Instance


    override fun onLoad() {
        super.onLoad()
        scriptManager.init()
    }


    override fun onEnable() {
        super.onEnable()
        logger.info("PuffTower enabled")
        DevoutServer.lamp.register(PuffTowerCommand())
        MinecraftServer.getGlobalEventHandler().addChild(puffTowerEventNode)
        basicSetup()
    }

    fun basicSetup() {
        // Create the instance
        val instanceManager = MinecraftServer.getInstanceManager()
        val instanceContainer = instanceManager.createInstanceContainer(AnvilLoader("test2"))

        // Set the ChunkGenerator so that it knows how to generate the world
        instanceContainer.setGenerator { unit: GenerationUnit -> unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK) }

        // Set the lighting system
        instanceContainer.chunkSupplier =
            ChunkSupplier { instance: Instance?, chunkX: Int, chunkZ: Int -> LightingChunk(instance, chunkX, chunkZ) }

        instance = instanceContainer

        // Add an event callback to specify the spawning instance (and the spawn position)
        val globalEventHandler = MinecraftServer.getGlobalEventHandler()
        globalEventHandler
            .addListener(AsyncPlayerConfigurationEvent::class.java) { event ->
                val player: Player = event.player
                event.spawningInstance = instanceContainer
                player.respawnPoint = Pos(0.0, 42.0, 0.0)
            }
        // features testing
        featureManager.forEach { id, feat ->
            logger.info("feature: $id loaded to basic instance")
            feat.hook(instance.eventNode())
        }
    }

}