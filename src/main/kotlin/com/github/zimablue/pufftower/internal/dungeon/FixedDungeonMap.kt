package com.github.zimablue.pufftower.internal.dungeon

import com.github.zimablue.pufftower.api.dungeon.DungeonMap
import net.minestom.server.instance.Instance
import java.nio.file.Path

class FixedDungeonMap(val map: Path) : DungeonMap() {
    override val instance: Instance
        get() {
            TODO("Not yet implemented")
        }
}