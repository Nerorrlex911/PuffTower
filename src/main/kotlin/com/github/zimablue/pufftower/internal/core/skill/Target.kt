package com.github.zimablue.pufftower.internal.core.skill

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity

class Target(
    val entityTarget: Entity?=null,
    val posTarget: Pos?=null //默认与释放者同一instance，跨instance释放技能？应该不存在这种情况吧。。。
) {
    val isEmpty: Boolean
        get() = entityTarget == null && posTarget == null
    companion object {
        fun empty() = Target(null,null)
    }
}