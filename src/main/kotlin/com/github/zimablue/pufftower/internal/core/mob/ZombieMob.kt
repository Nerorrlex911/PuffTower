package com.github.zimablue.pufftower.internal.core.mob

import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.entity.ai.goal.MeleeAttackGoal
import net.minestom.server.entity.ai.target.ClosestEntityTarget
import net.minestom.server.entity.attribute.Attribute
import net.minestom.server.utils.time.TimeUnit

class ZombieMob : Mob(EntityType.ZOMBIE, "zombie","僵尸") {
    init {
        addAIGroup(
            listOf(MeleeAttackGoal(this, 1.2, 20, TimeUnit.SERVER_TICK)),
            listOf(
                ClosestEntityTarget(
                    this, 32.0
                ) { entity: Entity? -> entity is Player },
            )
        )
        collidesWithEntities=true

        val movementSpeedAttribute = getAttribute(Attribute.MOVEMENT_SPEED)
        movementSpeedAttribute.baseValue = 0.1
        val healthAttribute = getAttribute(Attribute.MAX_HEALTH)
        healthAttribute.baseValue = 20.0
        heal()
    }

}