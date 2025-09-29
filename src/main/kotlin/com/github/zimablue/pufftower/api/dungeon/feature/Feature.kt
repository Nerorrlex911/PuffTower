package com.github.zimablue.pufftower.api.dungeon.feature

import net.minestom.server.event.EventNode
import net.minestom.server.event.trait.InstanceEvent

interface Feature {
    val priority: Int
        get() = 0
    fun hook(node: EventNode<InstanceEvent>)
}