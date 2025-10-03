package com.github.zimablue.pufftower.util

import com.github.zimablue.attrsystem.fight.api.FightAPI
import com.github.zimablue.attrsystem.fight.api.fight.FightData
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.damage.Damage
import taboolib.common5.cfloat
import kotlin.math.cos
import kotlin.math.sin

fun attack(attacker: LivingEntity, target: LivingEntity, force: Float=1.0f) : Double{
    //造成伤害
    val fightData = FightData(attacker, target).also {
        it["projectile"] = "false"
        it["fightData"] = it
        it["force"] = force.cfloat
    }
    val damage = FightAPI.runFight("attack-damage",fightData,message = true,damage = false)
    //击退
    if(damage<=0.0) return 0.0
    target.damage(Damage.fromEntity(attacker,damage.cfloat))
    val knockback = fightData.getOrDefault("knockback",0.4f).cfloat
    if(knockback<=0.0) return damage
    target.takeKnockback(
        knockback,
        sin(attacker.position.yaw() * (Math.PI / 180)),
        -cos(attacker.position.yaw() * (Math.PI / 180))
    )
    return damage
}