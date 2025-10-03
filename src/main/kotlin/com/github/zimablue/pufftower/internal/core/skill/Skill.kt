package com.github.zimablue.pufftower.internal.core.skill

import com.github.zimablue.devoutserver.util.map.component.Registrable
import com.github.zimablue.pufftower.PuffTower

interface Skill : Registrable<String> {

    fun execute(skillData: SkillData) : SkillResult

    override fun register() {
        PuffTower.skillManager.register(this)
    }
}