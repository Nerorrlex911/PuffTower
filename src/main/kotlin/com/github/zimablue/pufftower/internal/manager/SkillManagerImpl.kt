package com.github.zimablue.pufftower.internal.manager

import com.github.zimablue.attrsystem.fight.api.FightAPI
import com.github.zimablue.attrsystem.fight.api.fight.FightData
import com.github.zimablue.pufftower.PuffTower
import com.github.zimablue.pufftower.api.manager.SkillManager
import com.github.zimablue.pufftower.internal.core.skill.SkillAPI.getCondition
import com.github.zimablue.pufftower.internal.core.skill.SkillAPI.getSelector
import com.github.zimablue.pufftower.internal.core.skill.SkillAPI.getSkill
import com.github.zimablue.pufftower.internal.core.skill.SkillResult
import com.github.zimablue.pufftower.internal.core.skill.Target
import net.minestom.server.entity.LivingEntity
import taboolib.common5.cbool

object SkillManagerImpl: SkillManager() {
    init {
        /*
        执行一系列技能
        execute:
          - skill: testSkill
            condition: testCondition
            selector: testSelector
          - skill: testSkill2
            condition: testCondition2
            selector: testSelector2
         */
        register("execute") {
            val list = skillMeta["execute"] as? List<Map<String,Any>>
            if (list == null) {
                PuffTower.logger.info("incorrect meta format: $skillMeta")
                return@register SkillResult.NONE
            }
            list.forEach { meta ->
                val newData = copy().apply {
                    skill = meta["skill"].toString().getSkill()?:return@register SkillResult.NONE
                    meta["condition"].toString().getCondition()?.let {
                        condition { it(this,Target.empty()) }
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
                FightAPI.runFight(skillMeta["key"].toString(),fightData,skillMeta["message"].cbool,skillMeta["damage"].cbool)
            }
            SkillResult.SUCCESS
        }

    }
}