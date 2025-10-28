package com.github.zimablue.pufftower.internal.core.dungeon.feature

import com.github.zimablue.pufftower.api.dungeon.feature.AbstractFeature
import com.github.zimablue.pufftower.internal.annotations.AutoRegister
import net.minestom.server.event.EventNode
import net.minestom.server.event.trait.InstanceEvent

@AutoRegister
object EnergyFeature : AbstractFeature("energy") {
    override fun hook(node: EventNode<InstanceEvent>) {
        TODO("Not yet implemented")
    }
}