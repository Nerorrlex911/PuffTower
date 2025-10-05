package com.github.zimablue.pufftower.internal.core.skill

import com.github.zimablue.pufftower.PuffTower

object SkillAPI {
    fun String.getSkill() = PuffTower.skillManager[this]?:run { 
        PuffTower.logger.error("skill does not exist: $this")
        null
    }

    fun String.getCondition() = PuffTower.conditionManager[this]?:run {
        PuffTower.logger.error("condition does not exist: $this")
        null
    }

    fun String.getSelector() = PuffTower.selectorManager[this]?:run {
        PuffTower.logger.error("selector does not exist: $this")
        null
    }
}