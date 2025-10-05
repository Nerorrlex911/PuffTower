package com.github.zimablue.pufftower.internal.manager

import com.github.zimablue.attrsystem.fight.api.FightAPI
import com.github.zimablue.attrsystem.fight.api.fight.FightData
import com.github.zimablue.pufftower.PuffTower
import com.github.zimablue.pufftower.api.manager.SkillManager
import com.github.zimablue.pufftower.internal.core.skill.SkillAPI.getCondition
import com.github.zimablue.pufftower.internal.core.skill.SkillAPI.getSelector
import com.github.zimablue.pufftower.internal.core.skill.SkillAPI.getSkill
import com.github.zimablue.pufftower.internal.core.skill.SkillResult
import com.github.zimablue.pufftower.util.EffectUtil
import net.minestom.server.entity.LivingEntity
import taboolib.common5.cbool

object SkillManagerImpl: SkillManager() {
    init {
        /*
        执行一系列技能
        execute:
          skills:
          - skill: testSkill
            condition: testCondition
            selector: testSelector
            targetCondition: testCondition
          - skill: testSkill2
            condition: testCondition2
            selector: testSelector2
            targetCondition: testCondition
         */
        register("execute") {
            val list = skillMeta["skills"] as? List<Map<String,Any>>
            if (list == null) {
                PuffTower.logger.info("incorrect meta format: $skillMeta")
                return@register SkillResult.NONE
            }
            list.forEach { meta ->
                val newData = copy().apply {
                    skill = meta["skill"].toString().getSkill()?:return@register SkillResult.NONE
                    meta["condition"].toString().getCondition()?.let {
                        condition = it
                    }
                    meta["targetCondition"].toString().getCondition()?.let {
                        targetCondition = it
                    }
                    meta["selector"].toString().getSelector()?.let { selector = it }
                }
                val result = newData.execute()
                if(result == SkillResult.NONE) return@register SkillResult.NONE
            }
            SkillResult.SUCCESS
        }
        /*
        执行FightSystem的战斗计算，caster作为attacker、实体目标作为defender
        将伤害计算结果存入SkillData["run_fight.damage"]
        run_fight:
          key: attack_damage
          message: true
          damage: true
          context:
            projectile: false
            force: 1.0
            ...
         */
        register("run_fight") {
            if(caster !is LivingEntity) return@register SkillResult.FAILED
            inheritedTargets.forEach { target ->
                val defender = target.entityTarget as? LivingEntity?:return@forEach
                val fightData = FightData(caster,defender).also {
                    putAll(skillMeta["context"] as? Map<String,Any>?:emptyMap())
                }
                val damage = FightAPI.runFight(skillMeta["key"].toString(),fightData,skillMeta["message"].cbool,skillMeta["damage"].cbool)
                put("run_fight.damage",damage)
            }
            SkillResult.SUCCESS
        }
        /*
        在目标位置召唤闪电
        thunder:

         */
        register("thunder") {
            inheritedTargets.forEach { target ->
                if(target.isEntity) {
                    EffectUtil.playThunder(target.entityTarget!!.instance,target.entityTarget.position)
                } else {
                    if(caster==null) return@forEach
                    EffectUtil.playThunder(caster.instance,target.posTarget!!)
                }
            }
            SkillResult.SUCCESS
        }
        /*
        发射一个抛射物
        projectile:
         */
        register("projectile") {
            inheritedTargets.forEach { target ->

            }
            SkillResult.SUCCESS
        }

    }
}