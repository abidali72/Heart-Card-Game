package com.hearts.game.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.hearts.game.data.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private val Application.dataStore: DataStore<Preferences> by preferencesDataStore(name = "hearts_settings")

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStore = application.dataStore

    private object Keys {
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val MUSIC_ENABLED = booleanPreferencesKey("music_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val GAME_SPEED = stringPreferencesKey("game_speed")
        val SCORE_LIMIT = intPreferencesKey("score_limit")
        val AI_DIFFICULTY = stringPreferencesKey("ai_difficulty")
        val CARD_THEME = intPreferencesKey("card_theme")
        val TABLE_THEME = intPreferencesKey("table_theme")
        
        // Stats Keys
        val GAMES_PLAYED = intPreferencesKey("stats_games_played")
        val GAMES_WON = intPreferencesKey("stats_games_won")
        val TRICKS_WON = intPreferencesKey("stats_tricks_won")
        val MOON_SHOTS = intPreferencesKey("stats_moon_shots")
    }

    val settings: StateFlow<GameConfig> = dataStore.data
        .map { prefs ->
            GameConfig(
                soundEnabled = prefs[Keys.SOUND_ENABLED] ?: true,
                musicEnabled = prefs[Keys.MUSIC_ENABLED] ?: true,
                vibrationEnabled = prefs[Keys.VIBRATION_ENABLED] ?: true,
                gameSpeed = GameSpeed.valueOf(prefs[Keys.GAME_SPEED] ?: GameSpeed.NORMAL.name),
                scoreLimit = prefs[Keys.SCORE_LIMIT] ?: 100,
                aiDifficulty = AIDifficulty.valueOf(prefs[Keys.AI_DIFFICULTY] ?: AIDifficulty.MEDIUM.name),
                cardTheme = prefs[Keys.CARD_THEME] ?: 0,
                tableTheme = prefs[Keys.TABLE_THEME] ?: 0
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GameConfig())

    val gameStats: StateFlow<GameStats> = dataStore.data
        .map { prefs ->
            GameStats(
                gamesPlayed = prefs[Keys.GAMES_PLAYED] ?: 0,
                gamesWon = prefs[Keys.GAMES_WON] ?: 0,
                totalTricksWon = prefs[Keys.TRICKS_WON] ?: 0,
                moonShots = prefs[Keys.MOON_SHOTS] ?: 0
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GameStats())

    fun updateSound(enabled: Boolean) = updatePref { it[Keys.SOUND_ENABLED] = enabled }
    fun updateMusic(enabled: Boolean) = updatePref { it[Keys.MUSIC_ENABLED] = enabled }
    fun updateVibration(enabled: Boolean) = updatePref { it[Keys.VIBRATION_ENABLED] = enabled }
    fun updateGameSpeed(speed: GameSpeed) = updatePref { it[Keys.GAME_SPEED] = speed.name }
    fun updateScoreLimit(limit: Int) = updatePref { it[Keys.SCORE_LIMIT] = limit }
    fun updateDifficulty(difficulty: AIDifficulty) = updatePref { it[Keys.AI_DIFFICULTY] = difficulty.name }
    fun updateCardTheme(theme: Int) = updatePref { it[Keys.CARD_THEME] = theme }
    fun updateTableTheme(theme: Int) = updatePref { it[Keys.TABLE_THEME] = theme }

    fun recordGameResult(isWin: Boolean) = updatePref { prefs ->
        val currentPlayed = prefs[Keys.GAMES_PLAYED] ?: 0
        prefs[Keys.GAMES_PLAYED] = currentPlayed + 1
        
        if (isWin) {
            val currentWon = prefs[Keys.GAMES_WON] ?: 0
            prefs[Keys.GAMES_WON] = currentWon + 1
        }
    }

    fun recordMoonShot() = updatePref { prefs ->
        val current = prefs[Keys.MOON_SHOTS] ?: 0
        prefs[Keys.MOON_SHOTS] = current + 1
    }

    fun recordTrickWon() = updatePref { prefs ->
        val current = prefs[Keys.TRICKS_WON] ?: 0
        prefs[Keys.TRICKS_WON] = current + 1
    }

    private fun updatePref(block: MutablePreferences.(MutablePreferences) -> Unit) {
        viewModelScope.launch {
            dataStore.edit { prefs -> block(prefs, prefs) }
        }
    }
}
