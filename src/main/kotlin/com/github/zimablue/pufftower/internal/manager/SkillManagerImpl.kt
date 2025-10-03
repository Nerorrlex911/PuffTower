package com.github.zimablue.pufftower.internal.manager

import com.github.zimablue.pufftower.api.manager.SkillManager
import com.github.zimablue.pufftower.internal.core.skill.Skill
import com.github.zimablue.pufftower.internal.core.skill.SkillData
import com.github.zimablue.pufftower.internal.core.skill.SkillResult

object SkillManagerImpl: SkillManager() {
    override fun execute(skill: Skill, skillData: SkillData): SkillResult {
        return skill.execute(skillData)
    }
}