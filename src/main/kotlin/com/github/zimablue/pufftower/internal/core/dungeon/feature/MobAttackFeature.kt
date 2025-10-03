package com.github.zimablue.pufftower.internal.core.dungeon.feature

import com.github.zimablue.pufftower.api.dungeon.feature.AbstractFeature
import com.github.zimablue.pufftower.internal.annotations.AutoRegister
import com.github.zimablue.pufftower.util.attack
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.Player
import net.minestom.server.event.EventNode
import net.minestom.server.event.entity.EntityAttackEvent
import net.minestom.server.event.trait.InstanceEvent

@AutoRegister
object MobAttackFeature : AbstractFeature("mob_attack") {
    override fun hook(node: EventNode<InstanceEvent>) {
        node.addListener(EntityAttackEvent::class.java) { event ->
            val attacker = event.entity as? LivingEntity?:return@addListener
            if(attacker is Player) return@addListener
            attack(attacker, event.target as? LivingEntity?: return@addListener)
        }
    }
}