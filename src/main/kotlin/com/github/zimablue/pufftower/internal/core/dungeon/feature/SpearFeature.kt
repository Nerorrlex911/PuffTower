package com.github.zimablue.pufftower.internal.core.dungeon.feature

import com.github.zimablue.pufftower.internal.annotations.AutoRegister
import com.github.zimablue.pufftower.util.attack
import com.github.zimablue.pufftower.util.getAttrValue
import net.minestom.server.collision.CollisionUtils
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.Player
import net.minestom.server.event.entity.EntityAttackEvent
import java.util.function.Predicate

@AutoRegister
object SpearFeature : WeaponFeature("spear","spear") {
    override fun onAttack(event: EntityAttackEvent, itemType: String): Boolean {
        val player = event.entity as Player
        val baseRange = 4.5
        val range = baseRange*(player.getAttrValue("AttackRange")?:100.0)/100
        val target = event.target as? LivingEntity ?: return false
        attack(player,target,1.0f)
        player.getLineOfSightEntities(range) { it is LivingEntity && it != target && it != player }
            .forEach { (entity, distance) ->
                val force = calcForce(distance, range).toFloat()
                attack(player,entity as LivingEntity,force)
            }
        return true
    }

    private fun calcForce(distance:Double, range:Double):Double {
        val normalized = distance / range
        return (1.0 - normalized * normalized / 2)
    }

    private fun Entity.getLineOfSightEntities(range: Double, predicate: Predicate<Entity>): Map<Entity, Double> {
        val instance = this.instance?:return emptyMap()
        val start: Pos = this.position.withY(this.position.y() + this.eyeHeight)
        val startAsVec = start.asVec()
        val finalPredicate =
            Predicate<Entity> { e ->
                e !== this && e.boundingBox.boundingBoxRayIntersectionCheck(
                    startAsVec,
                    this.position.direction(), e.position
                ) && predicate.test(e) && CollisionUtils.isLineOfSightReachingShape(
                    instance,
                    this.chunk,
                    start,
                    e.position.withY(e.position.y() + e.eyeHeight),
                    e.boundingBox,
                    e.position
                )
            }
        val nearby = instance.getNearbyEntities(this.position, range).filter { finalPredicate.test(it) }
            .associateWith { it.getDistance(this) }
        return nearby
    }
}