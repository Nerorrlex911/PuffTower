package com.github.zimablue.pufftower.internal.core.dungeon.feature

import com.github.zimablue.attrsystem.api.AttrAPI
import com.github.zimablue.attrsystem.api.AttrAPI.getAttrData
import com.github.zimablue.pufftower.PuffTower
import com.github.zimablue.pufftower.api.dungeon.feature.AbstractFeature
import com.github.zimablue.pufftower.api.manager.WeaponManager
import com.github.zimablue.pufftower.api.manager.WeaponManager.Companion.itemType
import com.github.zimablue.pufftower.internal.annotations.AutoRegister
import com.github.zimablue.pufftower.util.attack
import com.github.zimablue.pufftower.util.getAttrValue
import net.minestom.server.component.DataComponents
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.Player
import net.minestom.server.event.EventNode
import net.minestom.server.event.entity.EntityAttackEvent
import net.minestom.server.event.trait.InstanceEvent
import net.minestom.server.network.packet.server.play.ParticlePacket
import net.minestom.server.particle.Particle

/**
 * 横扫类武器
 * 左键攻击产生扇形横扫伤害(受横扫范围属性影响)，伴有击退(受击退属性影响?)
 */
@AutoRegister
object SweepAttackFeature : AbstractFeature("sweep") {
    override fun hook(node: EventNode<InstanceEvent>) {
        node.addListener(EntityAttackEvent::class.java) { event ->
            val player = event.entity as? Player ?: return@addListener
            val weapon = player.itemInMainHand
            if(PuffTower.cooldownManager.hasCooldown(player,"weapon")) return@addListener
            val itemType = weapon.itemType
            if(itemType != "sword" && itemType != "axe") return@addListener
            val baseRange = if(itemType=="sword") 3.0 else 4.0
            val target = event.target as? LivingEntity ?: return@addListener
            val damage = attack(player,target)
            if(damage>0) sweepEffect(target)
            player.instance.getNearbyEntities(player.position, baseRange*(player.getAttrValue("AttackRange")?:100.0)/100).forEach { entity ->
                if(entity !is LivingEntity) return@forEach
                // 排除当前目标和自己
                if(entity==event.target) return@forEach
                if(entity==player) return@forEach
                //todo should pvp check here?
                val direction = entity.position.sub(player.position).asVec()
                val playerDirection = player.position.direction()
                val angle = Math.toDegrees(direction.angle(playerDirection))
                if(angle>60) return@forEach
                val entityDamage = attack(player,entity,1.0f)
                if(entityDamage>0) sweepEffect(entity)
            }
        }

    }

    fun sweepEffect(entity: LivingEntity) {
        val particle = ParticlePacket(
            Particle.SWEEP_ATTACK,
            entity.position.add(0.0, entity.eyeHeight*0.5, 0.0),
            Pos.ZERO,
            0.0f,
            1
        )
        entity.viewers.forEach { it.sendPacket(particle) }
    }




}