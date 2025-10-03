package com.github.zimablue.pufftower.internal.core.dungeon.feature

import com.github.zimablue.pufftower.api.dungeon.feature.AbstractFeature
import com.github.zimablue.pufftower.api.manager.WeaponManager
import com.github.zimablue.pufftower.internal.annotations.AutoRegister
import com.github.zimablue.pufftower.util.attack
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.Player
import net.minestom.server.event.EventNode
import net.minestom.server.event.entity.EntityAttackEvent
import net.minestom.server.event.trait.InstanceEvent

/**
 * 剑类武器
 * 左键攻击产生扇形横扫伤害(受攻击范围属性影响)，伴有击退(受击退属性影响)
 */
@AutoRegister
object SwordFeature : AbstractFeature("sword") {
    override fun hook(node: EventNode<InstanceEvent>) {
        node.addListener(EntityAttackEvent::class.java) { event ->
            val player = event.entity as? Player ?: return@addListener
            val weapon = player.itemInMainHand
            if(weapon.getTag(WeaponManager.ITEM_TYPE)!="sword") return@addListener
            attack(player,event.target as? LivingEntity ?: return@addListener)
            player.instance.getNearbyEntities(player.position, 3.0).forEach { entity ->
                if(entity !is LivingEntity) return@forEach
                // 排除当前目标和自己
                if(entity==event.target) return@forEach
                if(entity==player) return@forEach
                //todo should pvp check here?
                val direction = entity.position.sub(player.position).asVec()
                val playerDirection = player.position.direction()
                val angle = Math.toDegrees(direction.angle(playerDirection))
                if(angle>60) return@forEach
                attack(player,entity,1.0f)
            }
        }

    }




}