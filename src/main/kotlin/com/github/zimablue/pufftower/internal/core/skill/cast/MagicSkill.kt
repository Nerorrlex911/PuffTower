package com.github.zimablue.pufftower.internal.core.skill.cast

import com.github.zimablue.devoutserver.util.map.component.Registrable
import com.github.zimablue.pufftower.PuffTower
import com.github.zimablue.pufftower.internal.core.skill.SkillData
import com.github.zimablue.pufftower.internal.core.skill.SkillResult
import com.github.zimablue.pufftower.internal.core.skill.Target
import net.minestom.server.entity.Player

class MagicSkill(
    override val key: String,
    val shape: String,
    val touchRange: Double=8.0,
    val costEnergy: Double=0.0,
    val skill: (SkillData) -> SkillResult = { SkillResult.NONE }
) : Registrable<String> {


    fun cast(player: Player, target: List<Target>) : SkillResult {
        SkillData(
            caster = player,
            inheritedTargets = target,

        )
        return SkillResult.NONE
    }

    override fun register() {
        PuffTower.magicSkillManager.register(this)
    }
}