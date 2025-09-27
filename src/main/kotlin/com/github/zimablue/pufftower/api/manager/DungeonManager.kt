package com.github.zimablue.pufftower.api.manager

import com.github.zimablue.devoutserver.util.map.KeyMap
import com.github.zimablue.pufftower.api.dungeon.Dungeon
import java.util.UUID

abstract class DungeonManager : KeyMap<UUID,Dungeon>() {

}