package com.hearts.game.data.model

data class GameStats(
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0,
    val totalTricksWon: Int = 0,
    val moonShots: Int = 0,
    val perfectRounds: Int = 0
) {
    val winRate: Float
        get() = if (gamesPlayed > 0) (gamesWon.toFloat() / gamesPlayed) * 100 else 0f
}
