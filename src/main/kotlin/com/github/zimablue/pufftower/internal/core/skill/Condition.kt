package com.github.zimablue.pufftower.internal.core.skill

@FunctionalInterface
interface Condition {
    fun check(skillData: SkillData,target: Target) : Boolean
}