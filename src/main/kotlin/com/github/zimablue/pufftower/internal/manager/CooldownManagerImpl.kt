package com.github.zimablue.pufftower.internal.manager

import com.github.zimablue.pufftower.api.manager.CooldownManager
import net.minestom.server.ServerFlag
import net.minestom.server.entity.LivingEntity
import net.minestom.server.tag.Tag

object CooldownManagerImpl : CooldownManager() {
    private val ENTITY_COOLDOWN_TAG: Tag<MutableMap<String, Long>> = Tag.Transient<MutableMap<String,Long>>("entity_cooldown").defaultValue { mutableMapOf() }
    override fun hasCooldown(owner: LivingEntity, cooldownGroup: String): Boolean {
        return owner.getTag(ENTITY_COOLDOWN_TAG).containsKey(cooldownGroup)
    }

    override fun getCooldown(owner: LivingEntity, cooldownGroup: String): Long {
        val time = owner.getTag(ENTITY_COOLDOWN_TAG)[cooldownGroup] ?: return 0
        return (time-System.currentTimeMillis()).millisToTick()
    }

    override fun setCooldown(owner: LivingEntity, cooldownGroup: String, cooldown: Long) {
        owner.getTag(ENTITY_COOLDOWN_TAG)[cooldownGroup] = cooldown.tickToMillis()+System.currentTimeMillis()
    }

    override fun clearCooldown(owner: LivingEntity, cooldownGroup: String) {
        owner.getTag(ENTITY_COOLDOWN_TAG).remove(cooldownGroup)
    }

    private fun Long.millisToTick(): Long {
        return this/1000*ServerFlag.SERVER_TICKS_PER_SECOND
    }
    private fun Long.tickToMillis(): Long {
        return this*1000/ServerFlag.SERVER_TICKS_PER_SECOND
    }
}