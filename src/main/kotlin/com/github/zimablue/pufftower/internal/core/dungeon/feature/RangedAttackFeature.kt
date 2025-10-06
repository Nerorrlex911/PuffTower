package com.github.zimablue.pufftower.internal.core.dungeon.feature

import com.github.zimablue.pufftower.PuffTower
import com.github.zimablue.pufftower.api.dungeon.feature.AbstractFeature
import com.github.zimablue.pufftower.api.manager.WeaponManager.Companion.itemType
import com.github.zimablue.pufftower.util.attack
import com.github.zimablue.pufftower.util.getAttrValue
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.Player
import net.minestom.server.event.EventNode
import net.minestom.server.event.entity.EntityAttackEvent
import net.minestom.server.event.trait.InstanceEvent
import net.minestom.server.network.packet.server.SendablePacket
import net.minestom.server.network.packet.server.play.ParticlePacket
import net.minestom.server.particle.Particle
import kotlin.math.cos
import kotlin.math.sin

/*
AOE攻击，攻击以目标为球心，一定范围的怪物, 伤害随距离衰减
 */
object RangedAttackFeature : WeaponFeature("ranged","hammer") {
    override fun onAttack(event: EntityAttackEvent, itemType: String): Boolean {
        val player = event.entity as Player
        val baseRange = 4.5
        val range = baseRange*(player.getAttrValue("AttackRange")?:100.0)/100
        val target = event.target as? LivingEntity ?: return false
        attack(player,target,1.0f)
        rangedEffect(target,range)
        player.instance.getNearbyEntities(target.position, range).forEach { entity ->
            if(entity !is LivingEntity) return@forEach
            // 排除当前目标和自己
            if(entity==event.target) return@forEach
            if(entity==player) return@forEach
            val distance = entity.getDistance(player)
            if(distance > range) return@forEach
            val force = calcForce(distance, range).toFloat()
            attack(player,entity,force)
        }
        return true
    }

    private fun calcForce(distance:Double, range:Double):Double {
        val normalized = distance / range
        return (1.0 - normalized * normalized)
    }

    fun rangedEffect(entity: LivingEntity,range:Double) {
        val particles = mutableListOf<SendablePacket>()
        for(point in 0..12) {
            val angle = (point / 12.0) * 2 * Math.PI
            val x = cos(angle) * range
            val z = sin(angle) * range
            val particle = ParticlePacket(
                Particle.END_ROD,
                entity.position.add(0.0, 0.4, 0.0),
                Pos(x, 0.0, z),
                0.3f,
                1
            )
            particles.add(particle)
        }

        entity.viewers.forEach { it.sendPackets(particles) }
    }
}