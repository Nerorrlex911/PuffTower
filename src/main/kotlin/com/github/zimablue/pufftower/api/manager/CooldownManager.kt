package com.github.zimablue.pufftower.api.manager

import net.minestom.server.entity.LivingEntity

abstract class CooldownManager {
    abstract fun hasCooldown(owner: LivingEntity,cooldownGroup: String): Boolean

    /**
     * 获取剩余冷却时间，单位tick
     * 若没有冷却，返回0
     */
    abstract fun getCooldown(owner: LivingEntity,cooldownGroup: String): Long

    /**
     * 设置冷却时间，单位tick
     */
    abstract fun setCooldown(owner: LivingEntity,cooldownGroup: String,cooldown: Long)

    /**
     * 清除冷却
     */
    abstract fun clearCooldown(owner: LivingEntity,cooldownGroup: String)
}