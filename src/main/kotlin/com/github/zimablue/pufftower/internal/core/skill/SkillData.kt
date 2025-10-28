package com.github.zimablue.pufftower.internal.core.skill

import net.minestom.server.entity.Entity
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.util.concurrent.ConcurrentHashMap

class SkillData(
    val caster: Entity?=null,
    // 从外部继承而来的目标，可被技能内部调用选择器覆盖
    val inheritedTargets: List<Target> = listOf(),
    // 元数据，配置技能的参数
    val skillMeta: ConcurrentHashMap<String,Any> = ConcurrentHashMap(),
    val conditionMeta: ConcurrentHashMap<String,Any> = ConcurrentHashMap(),
    val targetConditionMeta: ConcurrentHashMap<String,Any> = ConcurrentHashMap(),
    val selectorMeta: ConcurrentHashMap<String,Any> = ConcurrentHashMap(),
    // 上下文，影响技能的表现
    context: Map<String,Any> = mapOf()
) : ConcurrentHashMap<String,Any>(context) {
    
    var skill: SkillData.() -> SkillResult = { SkillResult.NONE }

    fun skill(callback: SkillData.() -> SkillResult): SkillData {
        skill = callback
        return this
    }
    //大多数情况下，Metadata和skill是绑定的，应该同时设定
    fun skill(meta: Map<String,Any>,callback: SkillData.() -> SkillResult): SkillData {
        skillMeta.clear()
        skillMeta.putAll(meta)
        skill = callback
        return this
    }

    var condition: (SkillData, Target) -> Boolean = { _,_ -> true }

    fun condition(callback: (SkillData, Target) -> Boolean) : SkillData {
        condition = callback
        return this
    }

    fun condition(meta: Map<String,Any>,callback: (SkillData, Target) -> Boolean) : SkillData {
        conditionMeta.clear()
        conditionMeta.putAll(meta)
        condition = callback
        return this
    }
    
    var targetCondition: (SkillData,Target) -> Boolean = { _,_ -> true }

    fun targetCondition(callback: (SkillData,Target) -> Boolean) : SkillData {
        targetCondition = callback
        return this
    }

    fun targetCondition(meta: Map<String,Any>,callback: (SkillData,Target) -> Boolean) : SkillData {
        targetConditionMeta.clear()
        targetConditionMeta.putAll(meta)
        targetCondition = callback
        return this
    }
    
    var selector: SkillData.() -> List<Target> = { listOf() }

    fun selector(callback: SkillData.() -> List<Target>) : SkillData {
        selector = callback
        return this
    }

    fun selector(meta: Map<String,Any>,callback: SkillData.() -> List<Target>) : SkillData {
        selectorMeta.clear()
        selectorMeta.putAll(meta)
        selector = callback
        return this
    }
    
    fun execute() : SkillResult {
        val check = condition(this,Target(entityTarget = caster))
        if(!check) return SkillResult.FAILED
        val targets = selector(this).filter { targetCondition(this,it) }
        return skill(withTargets(targets))
    }

    fun withTargets(targets: List<Target>): SkillData {
        return SkillData(caster, targets, skillMeta, conditionMeta, targetConditionMeta, selectorMeta,this)
    }

    fun copy() = SkillData(caster, inheritedTargets, skillMeta, conditionMeta, targetConditionMeta, selectorMeta, this)

    companion object {
        fun emptyMeta() = Configuration.empty(Type.JSON,false)
    }
    
}