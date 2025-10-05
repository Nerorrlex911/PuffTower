package com.github.zimablue.pufftower.internal.core.skill

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity

// Target应当要么是实体目标要么是位置目标，不可以两个都是
open class Target(
    val entityTarget: Entity?=null,
    val posTarget: Pos?=null //默认与释放者同一instance，跨instance释放技能？应该不存在这种情况吧。。。
) {
    val isEmpty: Boolean
        get() = entityTarget == null && posTarget == null

    val isEntity: Boolean = entityTarget != null
    val isPos: Boolean = posTarget != null

    fun getPos() = if(entityTarget!=null) entityTarget.position else posTarget

    class EntityTarget(entity: Entity) : Target(entityTarget = entity,posTarget = null) {

    }

    class PosTarget(pos: Pos) : Target(entityTarget = null,posTarget = pos) {

    }
}