package com.github.zimablue.pufftower.api.dungeon

import com.github.zimablue.devoutserver.util.map.component.Registrable
import com.github.zimablue.pufftower.PuffTower
import com.github.zimablue.pufftower.api.team.Team
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.Instant
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

abstract class Dungeon : Registrable<UUID> {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override val key: UUID
        get() = team.captain

    private val _state = MutableStateFlow(GameState.CREATED)
    val state: StateFlow<GameState> get() = _state.asStateFlow()
    var startDate: Instant? = null
        private set
    var endDate: Instant? = null
        private set

    abstract val team: Team

    // 生命周期方法
    /**
     * Used to prepare the game for players e.g. generate the world, summon entities, register listeners, etc.
     * Players SHOULD NOT be altered in this state
     */
    abstract suspend fun init()
    /**
     * Used to start the game, here you can change the players' instance, etc.
     */
    abstract suspend fun onStart()
    /**
     * Used to reset the players after the game
     */
    abstract suspend fun onEnd()
    /**
     * Used to prepare the game for ending within the specified timeout
     *
     * @param timeout duration in which the game should end
     */
    abstract suspend fun onShutdown(timeout: Duration)
    /**
     * Called when the game didn't finish in time after [shutdown] has been called
     */
    open suspend fun kill() {
        scope.cancel()
    }

    /**
     * Used to start the game, the start sequence is the following (note that a shutdown will interrupt this flow):
     * 1. Set state to [GameState.INITIALIZING]
     * 2. Execute [init]
     * 3. Set state to [GameState.STARTING]
     * 4. Execute [onStart]
     * 5. Set state to [GameState.STARTED]
     *
     * @throws RuntimeException if called when the state isn't [GameState.CREATED]
     */
    fun start() {
        scope.launch {
            if (_state.value != GameState.CREATED) throw IllegalStateException("Cannot start twice!")
            _state.value = GameState.INITIALIZING
            init()
            if (_state.value != GameState.INITIALIZING) return@launch
            _state.value = GameState.STARTING
            onStart()
            if (_state.value != GameState.STARTING) return@launch
            _state.value = GameState.STARTED
            startDate = Instant.now()
        }
    }

    /**
     * Used to end the game normally, only the first call will execute [onEnd]
     * multiple calls to this method will be ignored
     */
    suspend fun end() {
        if (_state.value >= GameState.ENDING) return
        _state.value = GameState.ENDING
        onEnd()
        if (_state.value != GameState.ENDING) return
        _state.value = GameState.ENDED
        endDate = Instant.now()
    }

    /**
     * Used to shut down the game gracefully. The shutdown process is as follows:
     * 1. Call [onShutdown] with the timeout.
     * 2. If **(A)** the timeout wasn't reached, continue with the normal ending procedure by calling [end],
     *    or if it was reached, but **(B)** the game already ended, then return; otherwise, **(C)** kill the game.
     *
     */
    fun shutdown() {
        scope.launch {
            if (_state.value >= GameState.SHUTTING_DOWN) return@launch
            _state.value = GameState.SHUTTING_DOWN
            withTimeoutOrNull(END_TIMEOUT) {
                onShutdown(END_TIMEOUT)
            } ?: run {
                if (_state.value < GameState.KILLED) {
                    _state.value = GameState.KILLED
                    endDate = Instant.now()
                    kill()
                }
            }
            if (_state.value < GameState.ENDED) end()
            scope.cancel()
        }
    }

    override fun register() {
        PuffTower.dungeonManager.register(this)
    }

    enum class GameState {
        CREATED, INITIALIZING, STARTING, STARTED,
        ENDING, SHUTTING_DOWN, ENDED, KILLED;
    }

    companion object {
        val END_TIMEOUT: Duration = 10.minutes
    }
}


