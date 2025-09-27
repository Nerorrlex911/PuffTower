package com.github.zimablue.pufftower.internal.dungeon.feature

import com.github.zimablue.devoutserver.util.map.component.Registrable
import com.github.zimablue.pufftower.PuffTower

abstract class AbstractFeature(override val key: String) : Feature, Registrable<String> {
    override fun register() {
        PuffTower.featureManager.register(this)
    }
}