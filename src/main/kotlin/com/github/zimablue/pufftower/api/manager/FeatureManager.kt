package com.github.zimablue.pufftower.api.manager

import com.github.zimablue.devoutserver.util.map.KeyMap
import com.github.zimablue.pufftower.api.dungeon.feature.AbstractFeature

abstract class FeatureManager : KeyMap<String, AbstractFeature>() {
}