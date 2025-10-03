package com.github.zimablue.pufftower.internal.core.mob

import com.github.zimablue.devoutserver.util.colored
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.entity.attribute.Attribute
import net.minestom.server.event.entity.EntityDamageEvent

class TrainDummyMob: Mob(EntityType.ARMOR_STAND, "train_dummy","训练假人") {
    init {
        val healthAttribute = getAttribute(Attribute.MAX_HEALTH)
        healthAttribute.baseValue = 99999.0
        heal()
        eventNode().addListener(EntityDamageEvent::class.java) { event ->
            val source = event.damage.source as? Player?:return@addListener
            source.sendMessage("§c你对训练假人造成了 ${event.damage.amount} 点伤害".colored())
            if(event.damage.amount >= healthAttribute.baseValue) {
                event.damage.amount = (healthAttribute.baseValue - 1.0).toFloat()
            }
            heal()
        }
    }
}