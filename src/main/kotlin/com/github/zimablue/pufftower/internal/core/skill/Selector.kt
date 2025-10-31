package com.github.zimablue.pufftower.internal.core.skill

@FunctionalInterface
interface Selector {
    fun select(skillData: SkillData) : List<Target>
}