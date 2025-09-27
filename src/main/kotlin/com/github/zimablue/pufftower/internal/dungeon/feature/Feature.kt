package com.github.zimablue.pufftower.internal.dungeon.feature

import net.minestom.server.event.EventNode
import net.minestom.server.event.trait.InstanceEvent

@FunctionalInterface
interface Feature {
    fun hook(node: EventNode<InstanceEvent>)
}