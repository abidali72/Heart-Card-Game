package com.hearts.game.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.statsStore: DataStore<Preferences> by preferencesDataStore(name = "hearts_stats")

/**
 * Persistent storage for player statistics and achievements.
 */
class StatsRepository(private val context: Context) {

    private object Keys {
        val GAMES_PLAYED = intPreferencesKey("games_played")
        val GAMES_WON = intPreferencesKey("games_won")
        val TOTAL_POINTS = intPreferencesKey("total_points")
        val TOTAL_TRICKS = intPreferencesKey("total_tricks")
        val MOON_ATTEMPTS = intPreferencesKey("moon_attempts")
        val MOON_SUCCESSES = intPreferencesKey("moon_successes")
        val WIN_STREAK = intPreferencesKey("win_streak")
        val BEST_STREAK = intPreferencesKey("best_streak")
        val PERFECT_ROUNDS = intPreferencesKey("perfect_rounds")
    }

    data class PlayerStats(
        val gamesPlayed: Int = 0,
        val gamesWon: Int = 0,
        val totalPoints: Int = 0,
        val totalTricks: Int = 0,
        val moonAttempts: Int = 0,
        val moonSuccesses: Int = 0,
        val winStreak: Int = 0,
        val bestStreak: Int = 0,
        val perfectRounds: Int = 0
    ) {
        val winRate: Float get() = if (gamesPlayed > 0) gamesWon.toFloat() / gamesPlayed else 0f
        val avgPointsPerGame: Float get() = if (gamesPlayed > 0) totalPoints.toFloat() / gamesPlayed else 0f
    }

    val stats: Flow<PlayerStats> = context.statsStore.data.map { prefs ->
        PlayerStats(
            gamesPlayed = prefs[Keys.GAMES_PLAYED] ?: 0,
            gamesWon = prefs[Keys.GAMES_WON] ?: 0,
            totalPoints = prefs[Keys.TOTAL_POINTS] ?: 0,
            totalTricks = prefs[Keys.TOTAL_TRICKS] ?: 0,
            moonAttempts = prefs[Keys.MOON_ATTEMPTS] ?: 0,
            moonSuccesses = prefs[Keys.MOON_SUCCESSES] ?: 0,
            winStreak = prefs[Keys.WIN_STREAK] ?: 0,
            bestStreak = prefs[Keys.BEST_STREAK] ?: 0,
            perfectRounds = prefs[Keys.PERFECT_ROUNDS] ?: 0
        )
    }

    suspend fun recordGameResult(
        won: Boolean,
        totalPoints: Int,
        tricksWon: Int,
        moonAttempted: Boolean,
        moonSucceeded: Boolean,
        hadPerfectRound: Boolean
    ) {
        context.statsStore.edit { prefs ->
            prefs[Keys.GAMES_PLAYED] = (prefs[Keys.GAMES_PLAYED] ?: 0) + 1
            if (won) {
                prefs[Keys.GAMES_WON] = (prefs[Keys.GAMES_WON] ?: 0) + 1
                val currentStreak = (prefs[Keys.WIN_STREAK] ?: 0) + 1
                prefs[Keys.WIN_STREAK] = currentStreak
                if (currentStreak > (prefs[Keys.BEST_STREAK] ?: 0)) {
                    prefs[Keys.BEST_STREAK] = currentStreak
                }
            } else {
                prefs[Keys.WIN_STREAK] = 0
            }
            prefs[Keys.TOTAL_POINTS] = (prefs[Keys.TOTAL_POINTS] ?: 0) + totalPoints
            prefs[Keys.TOTAL_TRICKS] = (prefs[Keys.TOTAL_TRICKS] ?: 0) + tricksWon
            if (moonAttempted) {
                prefs[Keys.MOON_ATTEMPTS] = (prefs[Keys.MOON_ATTEMPTS] ?: 0) + 1
            }
            if (moonSucceeded) {
                prefs[Keys.MOON_SUCCESSES] = (prefs[Keys.MOON_SUCCESSES] ?: 0) + 1
            }
            if (hadPerfectRound) {
                prefs[Keys.PERFECT_ROUNDS] = (prefs[Keys.PERFECT_ROUNDS] ?: 0) + 1
            }
        }
    }
}
