package com.github.zimablue.pufftower.internal.core.dungeon.feature

import com.github.zimablue.pufftower.api.dungeon.feature.AbstractFeature
import com.github.zimablue.pufftower.api.manager.WeaponManager
import net.minestom.server.entity.PlayerHand
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerHandAnimationEvent
import net.minestom.server.event.trait.InstanceEvent

/**
 * 剑类武器
 * 左键攻击产生扇形横扫伤害(受攻击范围属性影响)，伴有击退(受击退属性影响)
 */
object SwordFeature : AbstractFeature("sword") {
    override fun hook(node: EventNode<InstanceEvent>) {
        node.addListener(PlayerHandAnimationEvent::class.java) { event ->
            if(event.hand != PlayerHand.MAIN) return@addListener
            val player = event.player
            val weapon = player.itemInMainHand
            if(weapon.getTag(WeaponManager.ITEM_TYPE)!="sword") return@addListener
        }
    }

}