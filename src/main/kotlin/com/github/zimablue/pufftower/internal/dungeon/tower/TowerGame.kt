package com.github.zimablue.pufftower.internal.dungeon.tower

import com.github.zimablue.pufftower.api.dungeon.Dungeon
import com.github.zimablue.pufftower.api.team.Team
import com.github.zimablue.pufftower.internal.manager.PTConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.minestom.server.instance.Instance
import kotlin.time.Duration

/**
 * the instance of a PuffTower game
 * stores data that is shared across all floors, including the lounge instance, statistics, etc.
 */
class TowerGame(override val team: Team) : Dungeon() {

    lateinit var loungeInstance : Instance
        private set

    fun updateLounge() {
        //todo update lounge scoreboard and statistics
        //todo generate loot chests
        //todo generate portals for next floor
        TODO()
    }

    fun setupNextFloor(floorConfig: FloorConfig) : TowerFloorDungeon {
        //todo setup the next floor dungeon( select floor config, calling init )
        TODO()
    }

    fun toNextFloor(floorConfig: FloorConfig) : TowerFloorDungeon {
        //todo setup next floor, call onStart of the next floor
        TODO()
    }

    override suspend fun init() {
        withContext(Dispatchers.IO) {
            loungeInstance=PTConfig.genLoungeInstance()
        }
        //todo prepare the lounge
        //todo prepare the first floor
        //todo teleport all players to the lounge
        TODO("Not yet implemented")
    }

    override suspend fun onStart() {
        // teleport all players to the tower lounge
        team.members.forEach { member ->
            member.setInstance(loungeInstance,PTConfig.loungeSpawn).join()
        }
        updateLounge()
        TODO("Not yet implemented")
    }

    override suspend fun onEnd() {
        TODO("Not yet implemented")
    }

    override suspend fun onShutdown(timeout: Duration) {
        TODO("Not yet implemented")
    }
}