package com.github.zimablue.pufftower.internal.core.skill

import net.minestom.server.entity.Entity
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.util.concurrent.ConcurrentHashMap

class SkillData(
    val caster: Entity?=null,
    // 从外部继承而来的目标，可被技能内部调用选择器覆盖
    val inheritedTargets: List<Target> = listOf(),
    // 元数据，配置技能的参数 TODO 用Map<String,Any>或者JsonObject、或者自定义的Metadata类，会不会更好？
    val metaData: ConfigurationSection = emptyMeta(),
    // 上下文，影响技能的表现
    context: Map<String,Any> = mapOf()
) : ConcurrentHashMap<String,Any>(context) {

    val skillMeta = metaData.getConfigurationSection("skill")?: emptyMeta()
    val conditionMeta = metaData.getConfigurationSection("condition")?: emptyMeta()
    val selectorMeta = metaData.getConfigurationSection("selector")?: emptyMeta()
    
    var skill: SkillData.() -> SkillResult = { SkillResult.NONE }

    fun skill(callback: SkillData.() -> SkillResult) {
        skill = callback
    }

    var condition: SkillData.() -> Boolean = { true }

    fun condition(callback: SkillData.() -> Boolean) {
        condition = callback
    }
    
    var targetCondition: (SkillData,Target) -> Boolean = { _,_ -> true }

    fun targetCondition(callback: (SkillData,Target) -> Boolean) {
        targetCondition = callback
    }
    
    var selector: SkillData.() -> List<Target> = { listOf() }

    fun selector(callback: SkillData.() -> List<Target>) {
        selector = callback
    }
    
    fun execute() : SkillResult {
        val check = condition(this)
        if(!check) return SkillResult.FAILED
        val targets = selector(this).filter { targetCondition(this,it) }
        return skill(SkillData(caster, targets, metaData,this))
    }

    companion object {
        fun emptyMeta() = Configuration.empty(Type.JSON,false)
    }
    
}