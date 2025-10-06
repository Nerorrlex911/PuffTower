package com.github.zimablue.pufftower.internal.core.dungeon.feature

import com.github.zimablue.pufftower.PuffTower
import com.github.zimablue.pufftower.api.dungeon.feature.AbstractFeature
import com.github.zimablue.pufftower.api.manager.WeaponManager.Companion.itemType
import net.minestom.server.entity.Player
import net.minestom.server.event.EventNode
import net.minestom.server.event.entity.EntityAttackEvent
import net.minestom.server.event.trait.InstanceEvent
import net.minestom.server.tag.Tag
import taboolib.common5.clong

abstract class WeaponFeature(key: String,vararg weaponTypes: String): AbstractFeature(key) {
    val itemTypes = weaponTypes.toMutableList()
    val COOLDOWN_TAG = Tag.Integer("cooldown").defaultValue(20)
    override fun hook(node: EventNode<InstanceEvent>) {
        node.addListener(EntityAttackEvent::class.java) { event ->
            val player = event.entity as? Player ?: return@addListener
            val weapon = player.itemInMainHand
            if (hasCooldown(player)) return@addListener
            val itemType = weapon.itemType?: return@addListener
            if (!itemTypes.contains(itemType)) return@addListener
            val cooldown = weapon.getTag(COOLDOWN_TAG)
            if(onAttack(event,itemType) && cooldown!=null) {
                setCooldown(player, cooldown.clong)
            }
        }
    }
    fun hasCooldown(player: Player): Boolean {
        return PuffTower.cooldownManager.hasCooldown(player, "weapon")
    }
    fun setCooldown(player: Player, cooldown: Long) {
        PuffTower.cooldownManager.setCooldown(player, "weapon", cooldown)
    }
    open fun onAttack(event: EntityAttackEvent,itemType: String) : Boolean {
        return false
    }
}