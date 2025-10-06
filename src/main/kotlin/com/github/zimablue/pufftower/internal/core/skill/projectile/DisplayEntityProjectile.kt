package com.github.zimablue.pufftower.internal.core.skill.projectile

import ca.atlasengine.projectiles.AbstractProjectile
import net.minestom.server.collision.Aerodynamics
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.instance.block.Block

class DisplayEntityProjectile(type: EntityType, shooter: Entity) : AbstractProjectile(type, shooter) {
    override fun shoot(from: Point, to: Point, power: Double, spread: Double) {
        TODO("Not yet implemented")
    }

    override fun updateVelocity(
        p0: Pos,
        p1: Vec,
        p2: Block.Getter,
        p3: Aerodynamics,
        p4: Boolean,
        p5: Boolean,
        p6: Boolean,
        p7: Boolean
    ): Vec {
        TODO("Not yet implemented")
    }
}