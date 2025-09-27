package com.github.zimablue.pufftower.api.dungeon

import net.minestom.server.instance.Instance

abstract class DungeonMap {
    /**
     * The Minestom instance representing the dungeon map.
     * This instance is responsible for the actual world where the dungeon takes place.
     */
    abstract val instance: Instance
}