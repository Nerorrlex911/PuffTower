package com.github.zimablue.pufftower.internal.core.skill

import net.minestom.server.entity.Entity
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.util.concurrent.ConcurrentHashMap

class SkillData(
    val caster: Entity?=null,
    val targets: List<Target> = listOf(),
    // 元数据，配置技能的参数
    val metadata: ConfigurationSection=Configuration.empty(Type.JSON,false),
    // 上下文，影响技能的表现
    context: Map<String,Any> = mapOf()
) : ConcurrentHashMap<String,Any>(context) {



}