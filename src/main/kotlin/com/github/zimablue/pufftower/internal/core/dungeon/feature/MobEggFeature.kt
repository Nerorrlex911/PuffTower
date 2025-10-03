package com.github.zimablue.pufftower.internal.core.dungeon.feature

import com.github.zimablue.pufftower.api.dungeon.feature.AbstractFeature
import com.github.zimablue.pufftower.internal.annotations.AutoRegister
import com.github.zimablue.pufftower.internal.core.mob.Mob
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerUseItemOnBlockEvent
import net.minestom.server.event.trait.InstanceEvent
import net.minestom.server.tag.Tag

@AutoRegister
object MobEggFeature: AbstractFeature("mob_egg") {
    private val MOB_EGG: Tag<String> = Tag.String("mob_egg")
    override fun hook(node: EventNode<InstanceEvent>) {
        node.addListener(PlayerUseItemOnBlockEvent::class.java) { event ->
            val player = event.player
            val item = event.itemStack
            if(!item.hasTag(MOB_EGG)) return@addListener
            val mobType = item.getTag(MOB_EGG) ?: return@addListener
            val entity = Mob.spawnMob(mobType)
            entity?.setInstance(player.instance,event.position.add(0.0,1.0,0.0))
        }
    }
}