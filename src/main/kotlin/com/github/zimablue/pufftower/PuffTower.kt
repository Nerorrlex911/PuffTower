package com.github.zimablue.pufftower

import com.github.zimablue.devoutserver.plugin.Plugin
import com.github.zimablue.pufftower.api.manager.DungeonManager
import com.github.zimablue.pufftower.api.manager.FeatureManager
import com.github.zimablue.pufftower.api.manager.GeneratorManager
import com.github.zimablue.pufftower.internal.manager.DungeonManagerImpl
import com.github.zimablue.pufftower.internal.manager.FeatureManagerImpl
import com.github.zimablue.pufftower.internal.manager.GeneratorManagerImpl
import com.github.zimablue.pufftower.internal.manager.ScriptManager

object PuffTower : Plugin() {

    val dungeonManager: DungeonManager = DungeonManagerImpl
    val generatorManager: GeneratorManager = GeneratorManagerImpl
    val featureManager: FeatureManager = FeatureManagerImpl
    val scriptManager: ScriptManager = ScriptManager

    override fun onEnable() {
        super.onEnable()
        logger.info("PuffTower enabled")
    }

}