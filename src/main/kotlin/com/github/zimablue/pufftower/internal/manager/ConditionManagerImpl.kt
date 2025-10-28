package com.github.zimablue.pufftower.internal.manager

import com.github.zimablue.pufftower.api.manager.ConditionManager
import com.github.zimablue.pufftower.internal.manager.EnergyManagerImpl.energy
import net.minestom.server.entity.Player
import taboolib.common5.cdouble

object ConditionManagerImpl : ConditionManager() {
    init {
        register("energy") { skillData, target ->
            if(!target.isPlayer) return@register false
            val require = skillData.conditionMeta["require"].cdouble
            require <= (target.entityTarget as Player).energy
        }
    }
}