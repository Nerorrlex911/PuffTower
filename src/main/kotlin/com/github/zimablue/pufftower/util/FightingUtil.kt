package com.github.zimablue.pufftower.util

import com.github.zimablue.attrsystem.fight.api.FightAPI
import com.github.zimablue.attrsystem.fight.api.fight.FightData
import net.minestom.server.entity.LivingEntity
import taboolib.common5.cfloat

fun attack(attacker: LivingEntity, target: LivingEntity, force: Float=1.0f) : Double{
    //造成伤害
    val fightData = FightData(attacker, target).also {
        it["projectile"] = "false"
        it["fightData"] = it
        it["force"] = force.cfloat
    }
    val damage = FightAPI.runFight("attack_damage",fightData,message = true,damage = true)
    //击退
    if(damage<=0.0) return 0.0
    val knockback = fightData["knockback"].cfloat
    if(knockback<=0.0) return damage
    target.takeKnockback(knockback,attacker.position.x, attacker.position.z)
    return damage
}