package com.github.zimablue.pufftower.internal.core.skill

import net.minestom.server.entity.Entity
import java.util.concurrent.ConcurrentHashMap

class SkillData(
    val caster: Entity?=null,
    val targets: List<Target> = emptyList(),
    context: Map<String, Any> = emptyMap(),
) : ConcurrentHashMap<String,Any>(context) {
}