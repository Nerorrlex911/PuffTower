package com.github.zimablue.pufftower.internal.core.skill

@FunctionalInterface
interface Mechanic {
    fun execute(skillData: SkillData) : SkillResult
}