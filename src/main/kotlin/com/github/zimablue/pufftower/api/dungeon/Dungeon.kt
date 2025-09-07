package com.github.zimablue.pufftower.api.dungeon

import com.github.zimablue.pufftower.api.team.Team

/**
 * Represents a dungeon in the PuffTower game.
 */
abstract class Dungeon (val key: String, val team: Team) {

    /**
     * The map of the dungeon
     * responsible for generating and maintaining the Minestom instance
     */
    abstract val map: DungeonMap

    /**
     * The context of the dungeon
     * dungeons can adjust their behavior based on the context
     */
    val context: DungeonContext = DungeonContext()

    /**
     * Starts the dungeon
     * This method is called when the dungeon is started
     */
    abstract fun start()

}