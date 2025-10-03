package com.github.zimablue.pufftower.api.manager

import com.github.zimablue.devoutserver.util.map.KeyMap
import com.github.zimablue.pufftower.internal.core.skill.Skill
import com.github.zimablue.pufftower.internal.core.skill.SkillData
import com.github.zimablue.pufftower.internal.core.skill.SkillResult
import com.github.zimablue.pufftower.internal.core.skill.Target
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type

abstract class SkillManager : KeyMap<String,Skill>() {

    abstract fun execute(skill: Skill, skillData: SkillData) : SkillResult

    fun execute(skill: String, skillData: SkillData) : SkillResult {
        return get(skill)?.let { execute(it, skillData) }?:return SkillResult.NONE
    }

    fun execute(
        skill: String,
        caster: Entity?=null,
        targets: List<Target> = listOf(),
        metadata: ConfigurationSection=Configuration.empty(Type.JSON,false),
        context: Map<String,Any> = mapOf()
    ): SkillResult {
        return execute(skill,SkillData(caster,targets,metadata,context))
    }

    fun execute(
        skill: String,
        caster: Entity?=null,
        targets: List<Entity> = listOf(),
        metadata: ConfigurationSection=Configuration.empty(Type.JSON,false),
        context: Map<String,Any> = mapOf()
    ): SkillResult {
        return execute(skill,SkillData(caster,targets.map{Target(entityTarget = it)},metadata,context))
    }

    fun execute(
        skill: String,
        caster: Entity?=null,
        targets: List<Pos> = listOf(),
        metadata: ConfigurationSection=Configuration.empty(Type.JSON,false),
        context: Map<String,Any> = mapOf()
    ): SkillResult {
        return execute(skill,SkillData(caster,targets.map{Target(posTarget = it)},metadata,context))
    }
}