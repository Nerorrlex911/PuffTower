package com.github.zimablue.pufftower.internal.core.dungeon.feature

import com.github.zimablue.pufftower.internal.annotations.AutoRegister
import com.github.zimablue.pufftower.util.attack
import com.github.zimablue.pufftower.util.getAttrValue
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.Player
import net.minestom.server.event.entity.EntityAttackEvent
import net.minestom.server.network.packet.server.play.ParticlePacket
import net.minestom.server.particle.Particle
import net.minestom.server.timer.TaskSchedule
import top.zoyn.particlelib.pobject.Arc

/**
 * 横扫类武器
 * 左键攻击产生扇形横扫伤害(受横扫范围属性影响)，伴有击退(受击退属性影响?)
 */
@AutoRegister
//TODO 空挥也应当触发横扫
object SweepAttackFeature : WeaponFeature("sweep","sword","axe") {
    override fun onAttack(event: EntityAttackEvent, itemType: String): Boolean {
        val player = event.entity as Player
        val baseRange = if(itemType=="sword") 3.0 else 4.0
        val target = event.target as? LivingEntity ?: return false
        val damage = attack(player,target)
        val range = baseRange*(player.getAttrValue("AttackRange")?:100.0)/100
        sweepEffect(player,range)
        if(damage>0) sweepDamageEffect(target)
        player.instance.getNearbyEntities(player.position, range).forEach { entity ->
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
            if(entityDamage>0) sweepDamageEffect(entity)
        }
        return true
    }

    fun sweepEffect(player: Player, range: Double) {
        val arc = Arc(
            player.position.add(0.0, player.eyeHeight, 0.0).add(Vec(-1.5,0.0,0.0).rotateFromView(player.position)),
            0.0,
            100.0,
            range,
            5.0,
        )
        arc.setParticleSpawner { pos, viewable ->
            val particle = ParticlePacket(
                Particle.CLOUD,
                pos,
                Pos.ZERO,
                0.0f,
                1
            )
            player.viewers.forEach { it.sendPacket(particle) }
        }
        player.scheduler().submitTask {
            if(arc.hasNext()) {
                arc.playNextPoint()
                TaskSchedule.nextTick()
            } else {
                TaskSchedule.stop()
            }
        }
    }

    fun sweepDamageEffect(entity: LivingEntity) {
        val particle = ParticlePacket(
            Particle.EXPLOSION,
            entity.position.add(0.0, entity.eyeHeight*0.5, 0.0),
            Pos.ZERO,
            0.0f,
            1
        )
        entity.viewers.forEach { it.sendPacket(particle) }
    }




}