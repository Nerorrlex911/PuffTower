package com.github.zimablue.pufftower.internal.core.dungeon.feature

import com.github.zimablue.pufftower.api.dungeon.feature.AbstractFeature
import net.minestom.server.event.EventNode
import net.minestom.server.event.trait.InstanceEvent

/**
 * 左手持法术书，右手持法杖在空中绘制符文以释放法术书上的特定法术
 * 原则上每种类型的法术对应一个符文图案，一本法术书上不能有重复的符文
 */
object MagicCastFeature : AbstractFeature("magic_cast") {
    override fun hook(node: EventNode<InstanceEvent>) {
        TODO("Not yet implemented")
    }
}