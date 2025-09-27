package com.github.zimablue.pufftower.api.manager

import com.github.zimablue.devoutserver.util.map.KeyMap
import com.github.zimablue.pufftower.internal.dungeon.generator.MobGenerator

abstract class GeneratorManager: KeyMap<String, MobGenerator>() {
}