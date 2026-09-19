package com.example.circlepop.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.circlepop.data.GameStats
import com.example.circlepop.data.SettingsRepository
import com.example.circlepop.data.StatsRepository
import com.example.circlepop.data.ThemeMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class CirclePopViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepository = SettingsRepository(application)
    private val statsRepository = StatsRepository(application)
    private val soundManager = SoundManager(application)
    private val haptics = HapticsController(application)

    private val _state = MutableStateFlow(CirclePopGameState())
    val state: StateFlow<CirclePopGameState> = _state.asStateFlow()

    val stats: StateFlow<GameStats> = statsRepository.stats
        .stateIn(viewModelScope, SharingStarted.Eagerly, GameStats())

    val themeMode: StateFlow<ThemeMode> = settingsRepository.themeMode
        .stateIn(viewModelScope, SharingStarted.Eagerly, ThemeMode.SYSTEM)

    val soundEnabled: StateFlow<Boolean> = settingsRepository.soundEnabled
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val hapticsEnabled: StateFlow<Boolean> = settingsRepository.hapticsEnabled
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    /** Emits the just-popped target so the UI can play a one-shot pop effect at its location. */
    private val _popEvents = MutableSharedFlow<CircleTarget>(extraBufferCapacity = 4)
    val popEvents: SharedFlow<CircleTarget> = _popEvents.asSharedFlow()

    /** One-shot signal that the run has finished and stats are saved; UI should navigate. */
    private val _navigateToResult = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToResult: SharedFlow<Unit> = _navigateToResult.asSharedFlow()

    private var lifetimeJob: Job? = null
    private var nextTargetId = 0L
    private var previousPosition: Pair<Float, Float>? = null
    private var previousColorIndex: Int = -1
    private var playAreaWidthDp = 300f
    private var playAreaHeightDp = 400f

    /** Called every composition of the play area so spawns always use the latest size. */
    fun updatePlayAreaSize(widthDp: Float, heightDp: Float) {
        if (widthDp > 0f) playAreaWidthDp = widthDp
        if (heightDp > 0f) playAreaHeightDp = heightDp
    }

    fun startGame() {
        lifetimeJob?.cancel()
        previousPosition = null
        previousColorIndex = -1
        _state.value = CirclePopGameState(isGameActive = true)
        spawnNextTarget(currentScore = 0)
    }

    fun onTargetTapped(targetId: Long) {
        val current = _state.value
        val target = current.target ?: return
        if (target.id != targetId) return

        lifetimeJob?.cancel()

        val newScore = current.score + 1
        val newStreak = current.streak + 1
        _state.update { it.copy(score = newScore, streak = newStreak, target = null) }

        viewModelScope.launch { _popEvents.emit(target) }

        if (soundEnabled.value) soundManager.playPop()
        if (hapticsEnabled.value) haptics.lightPop()

        spawnNextTarget(currentScore = newScore)
    }

    /** Player backed out of an active run. Stats are intentionally not persisted. */
    fun leaveGameEarly() {
        lifetimeJob?.cancel()
        _state.update { CirclePopGameState() }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { settingsRepository.setThemeMode(mode) }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setSoundEnabled(enabled) }
    }

    fun setHapticsEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setHapticsEnabled(enabled) }
    }

    fun resetStats() {
        viewModelScope.launch { statsRepository.resetStats() }
    }

    private fun spawnNextTarget(currentScore: Int) {
        lifetimeJob?.cancel()

        val sizeDp = Difficulty.targetSizeDpFor(currentScore)
        val lifetimeMs = Difficulty.lifetimeMillisFor(currentScore)

        val (xFraction, yFraction) = TargetPlacement.randomPosition(
            containerWidthDp = playAreaWidthDp,
            containerHeightDp = playAreaHeightDp,
            circleSizeDp = sizeDp,
            previous = previousPosition
        )
        previousPosition = xFraction to yFraction

        var colorIndex = Random.nextInt(GameConstants.TARGET_COLOR_COUNT)
        if (colorIndex == previousColorIndex) {
            colorIndex = (colorIndex + 1) % GameConstants.TARGET_COLOR_COUNT
        }
        previousColorIndex = colorIndex

        val id = ++nextTargetId
        val newTarget = CircleTarget(
            id = id,
            xFraction = xFraction,
            yFraction = yFraction,
            sizeDp = sizeDp,
            colorIndex = colorIndex
        )

        _state.update { it.copy(target = newTarget, missedTarget = null, lifetimeProgress = 1f) }

        lifetimeJob = viewModelScope.launch {
            val startTime = System.nanoTime()
            val durationNanos = lifetimeMs * 1_000_000L
            while (isActive) {
                val elapsed = System.nanoTime() - startTime
                val progress = (1f - elapsed.toFloat() / durationNanos).coerceIn(0f, 1f)
                _state.update { current ->
                    if (current.target?.id == id) current.copy(lifetimeProgress = progress) else current
                }
                if (elapsed >= durationNanos) break
                delay(GameConstants.PROGRESS_TICK_MS)
            }
            if (_state.value.target?.id == id) {
                handleMissed(newTarget)
            }
        }
    }

    private fun handleMissed(target: CircleTarget) {
        lifetimeJob?.cancel()
        _state.update { it.copy(target = null, missedTarget = target, isGameActive = false, lifetimeProgress = 0f) }

        if (soundEnabled.value) soundManager.playMiss()
        if (hapticsEnabled.value) haptics.strongMiss()

        val finalScore = _state.value.score
        val finalStreak = _state.value.streak

        viewModelScope.launch {
            delay(GameConstants.MISS_FEEDBACK_DURATION_MS)
            val (_, isNewBest) = statsRepository.recordRunFinished(finalScore, finalStreak)
            if (isNewBest) {
                if (soundEnabled.value) soundManager.playNewBest()
                if (hapticsEnabled.value) haptics.successPulse()
            }
            _state.update { it.copy(isNewBest = isNewBest) }
            _navigateToResult.emit(Unit)
        }
    }

    override fun onCleared() {
        super.onCleared()
        lifetimeJob?.cancel()
        soundManager.release()
    }
}
