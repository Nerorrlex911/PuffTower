package com.github.zimablue.pufftower.api.manager

import net.minestom.server.entity.Player

abstract class EnergyManager {
    abstract fun getMaxEnergy(player: Player) : Double
    abstract fun getEnergy(player: Player) : Double
    abstract fun setEnergy(player: Player, health: Double)
}