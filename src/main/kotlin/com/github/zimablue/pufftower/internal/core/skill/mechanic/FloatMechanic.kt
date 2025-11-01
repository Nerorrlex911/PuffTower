package com.github.zimablue.pufftower.internal.core.skill.mechanic

import com.github.zimablue.pufftower.internal.core.skill.Mechanic
import com.github.zimablue.pufftower.internal.core.skill.SkillData
import com.github.zimablue.pufftower.internal.core.skill.SkillResult

class FloatMechanic(
    private val power: Double = 0.1,
) : Mechanic {
    override fun execute(skillData: SkillData): SkillResult {
        val caster = skillData.caster ?: return SkillResult.SUCCESS
        if(caster.hasNoGravity()) {
            caster.velocity.withY(power)
        }
        return SkillResult.SUCCESS
    }
}