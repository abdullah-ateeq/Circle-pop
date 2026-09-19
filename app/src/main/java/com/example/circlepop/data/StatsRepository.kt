package com.example.circlepop.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.statsDataStore by preferencesDataStore(name = "circle_pop_stats")

data class GameStats(
    val bestScore: Int = 0,
    val bestStreak: Int = 0,
    val gamesPlayed: Int = 0
)

/** Persists cross-session gameplay statistics. Only written once per finished run. */
class StatsRepository(private val context: Context) {

    private object Keys {
        val BEST_SCORE = intPreferencesKey("best_score")
        val BEST_STREAK = intPreferencesKey("best_streak")
        val GAMES_PLAYED = intPreferencesKey("games_played")
    }

    val stats: Flow<GameStats> = context.statsDataStore.data.map { prefs ->
        GameStats(
            bestScore = prefs[Keys.BEST_SCORE] ?: 0,
            bestStreak = prefs[Keys.BEST_STREAK] ?: 0,
            gamesPlayed = prefs[Keys.GAMES_PLAYED] ?: 0
        )
    }

    suspend fun currentStats(): GameStats = stats.first()

    /**
     * Records the result of a finished run in a single write, returning the updated
     * stats and whether this run set a new best score.
     */
    suspend fun recordRunFinished(finalScore: Int, finalStreak: Int): Pair<GameStats, Boolean> {
        var isNewBest = false
        var updated = GameStats()
        context.statsDataStore.edit { prefs ->
            val previousBestScore = prefs[Keys.BEST_SCORE] ?: 0
            val previousBestStreak = prefs[Keys.BEST_STREAK] ?: 0
            val previousGamesPlayed = prefs[Keys.GAMES_PLAYED] ?: 0

            isNewBest = finalScore > previousBestScore
            val newBestScore = maxOf(previousBestScore, finalScore)
            val newBestStreak = maxOf(previousBestStreak, finalStreak)
            val newGamesPlayed = previousGamesPlayed + 1

            prefs[Keys.BEST_SCORE] = newBestScore
            prefs[Keys.BEST_STREAK] = newBestStreak
            prefs[Keys.GAMES_PLAYED] = newGamesPlayed

            updated = GameStats(newBestScore, newBestStreak, newGamesPlayed)
        }
        return updated to isNewBest
    }

    suspend fun resetStats() {
        context.statsDataStore.edit { prefs ->
            prefs[Keys.BEST_SCORE] = 0
            prefs[Keys.BEST_STREAK] = 0
            prefs[Keys.GAMES_PLAYED] = 0
        }
    }
}
