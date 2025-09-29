package com.github.zimablue.pufftower

import com.github.zimablue.devoutserver.DevoutServer
import com.github.zimablue.devoutserver.plugin.Plugin
import com.github.zimablue.devoutserver.plugin.script.PluginScriptManager
import com.github.zimablue.pufftower.api.manager.DungeonManager
import com.github.zimablue.pufftower.api.manager.FeatureManager
import com.github.zimablue.pufftower.internal.core.command.PuffTowerCommand
import com.github.zimablue.pufftower.internal.manager.DungeonManagerImpl
import com.github.zimablue.pufftower.internal.manager.FeatureManagerImpl
import net.minestom.server.MinecraftServer
import net.minestom.server.event.EventNode

object PuffTower : Plugin() {

    val puffTowerEventNode = EventNode.all("PuffTower").setPriority(15)

    val dungeonManager: DungeonManager = DungeonManagerImpl
    val featureManager: FeatureManager = FeatureManagerImpl
    val scriptManager: PluginScriptManager = PluginScriptManager(this)


    override fun onLoad() {
        super.onLoad()
        scriptManager.init()
    }


    override fun onEnable() {
        super.onEnable()
        logger.info("PuffTower enabled")
        DevoutServer.lamp.register(PuffTowerCommand())
        MinecraftServer.getGlobalEventHandler().addChild(puffTowerEventNode)
    }

}