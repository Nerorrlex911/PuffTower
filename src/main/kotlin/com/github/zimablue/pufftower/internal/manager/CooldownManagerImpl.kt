package com.github.zimablue.pufftower.internal.manager

import com.github.zimablue.pufftower.api.manager.CooldownManager
import net.minestom.server.entity.LivingEntity

object CooldownManagerImpl : CooldownManager() {
    override fun hasCooldown(owner: LivingEntity, cooldownGroup: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getCooldown(owner: LivingEntity, cooldownGroup: String): Long {
        TODO("Not yet implemented")
    }

    override fun setCooldown(owner: LivingEntity, cooldownGroup: String, cooldown: Long) {
        TODO("Not yet implemented")
    }

    override fun clearCooldown(owner: LivingEntity, cooldownGroup: String) {
        TODO("Not yet implemented")
    }
}