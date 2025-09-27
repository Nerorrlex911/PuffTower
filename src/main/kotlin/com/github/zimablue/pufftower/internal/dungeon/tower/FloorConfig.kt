package com.github.zimablue.pufftower.internal.dungeon.tower

import com.github.zimablue.pufftower.PuffTower
import com.github.zimablue.pufftower.internal.dungeon.feature.Feature
import com.github.zimablue.pufftower.internal.dungeon.generator.MobGenerator
import com.github.zimablue.pufftower.util.toPos
import net.minestom.server.coordinate.Pos
import taboolib.library.configuration.ConfigurationSection
import java.nio.file.Path

class FloorConfig(
    val mapPath: Path,
    val spawnPosition: Pos,
    val generators: List<MobGenerator>,
    val features: List<Feature>,
    val onStart: (TowerFloorDungeon)->Unit={},
    val onEnd: (TowerFloorDungeon)->Unit={},
    val onShutdown: (TowerFloorDungeon)->Unit = {},
) {

    companion object {
        fun fromConfig(conf: ConfigurationSection): FloorConfig {
            val onStart = conf.getString("onStart")
            val onEnd = conf.getString("onEnd")
            val onShutdown = conf.getString("onShutdown")
            return FloorConfig(
                mapPath = Path.of(conf.getString("mapPath")!!),
                spawnPosition = conf.getDoubleList("spawnPosition").toPos(),
                generators = conf.getStringList("generators").mapNotNull { PuffTower.generatorManager[it] },
                onStart = {floor -> onStart?.let { PuffTower.scriptManager.pluginScriptManager.run(it,null, floor) } },
                onEnd = {floor -> onEnd?.let { PuffTower.scriptManager.pluginScriptManager.run(it,null, floor) } },
                onShutdown = {floor -> onShutdown?.let { PuffTower.scriptManager.pluginScriptManager.run(it,null,floor) } },
                features = conf.getStringList("features").mapNotNull { PuffTower.featureManager[it] }
            )
        }
    }
}