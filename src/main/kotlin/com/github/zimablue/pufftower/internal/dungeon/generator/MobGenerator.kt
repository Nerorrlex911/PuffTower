package com.github.zimablue.pufftower.internal.dungeon.generator

import com.github.zimablue.devoutserver.util.map.component.Registrable
import com.github.zimablue.pufftower.PuffTower
import com.github.zimablue.pufftower.internal.dungeon.tower.TowerFloorDungeon
import net.minestom.server.entity.Entity

/**
 * generator for mobs in the dungeon
 */
abstract class MobGenerator : Registrable<String> {

    abstract fun generate(towerFloor: TowerFloorDungeon) : List<Entity>

    override fun register() {
        PuffTower.generatorManager.register(this)
    }

}