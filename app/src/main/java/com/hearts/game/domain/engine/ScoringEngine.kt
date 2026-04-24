package com.hearts.game.domain.engine

import com.hearts.game.data.model.*

/**
 * Handles all scoring calculations including Shoot the Moon detection.
 */
object ScoringEngine {

    private const val TOTAL_HEART_POINTS = 13
    private const val QUEEN_SPADES_POINTS = 13
    private const val MOON_POINTS = TOTAL_HEART_POINTS + QUEEN_SPADES_POINTS // 26

    /**
     * Calculate the total point value of cards in a trick.
     */
    fun calculateTrickPoints(trick: Trick): Int {
        return trick.plays.sumOf { it.card.pointValue }
    }

    /**
     * Calculates each player's round score from their won tricks.
     */
    fun calculateRoundScores(
        trickResults: Map<PlayerPosition, List<Trick>>
    ): Map<PlayerPosition, Int> {
        return PlayerPosition.allPositions.associateWith { position ->
            val tricks = trickResults[position] ?: emptyList()
            tricks.sumOf { trick ->
                trick.plays.sumOf { it.card.pointValue }
            }
        }
    }

    /**
     * Detects if any player shot the moon (collected all 26 points).
     * Returns the position of the moon shooter, or null.
     */
    fun detectShootTheMoon(roundScores: Map<PlayerPosition, Int>): PlayerPosition? {
        return roundScores.entries.find { it.value == MOON_POINTS }?.key
    }

    /**
     * Applies Shoot the Moon scoring: shooter gets 0, all others get +26.
     */
    fun applyMoonScoring(
        roundScores: Map<PlayerPosition, Int>,
        moonShooter: PlayerPosition
    ): Map<PlayerPosition, Int> {
        return PlayerPosition.allPositions.associateWith { position ->
            if (position == moonShooter) 0 else MOON_POINTS
        }
    }

    /**
     * Calculates final round scores, automatically applying moon scoring if detected.
     * Returns a pair of (adjusted scores, moon shooter if any).
     */
    fun finalizeRoundScores(
        trickResults: Map<PlayerPosition, List<Trick>>
    ): Pair<Map<PlayerPosition, Int>, PlayerPosition?> {
        val rawScores = calculateRoundScores(trickResults)
        val moonShooter = detectShootTheMoon(rawScores)

        val finalScores = if (moonShooter != null) {
            applyMoonScoring(rawScores, moonShooter)
        } else {
            rawScores
        }

        return Pair(finalScores, moonShooter)
    }

    /**
     * Updates cumulative scores and returns the new totals.
     */
    fun updateCumulativeScores(
        currentTotals: Map<PlayerPosition, Int>,
        roundScores: Map<PlayerPosition, Int>
    ): Map<PlayerPosition, Int> {
        return PlayerPosition.allPositions.associateWith { position ->
            (currentTotals[position] ?: 0) + (roundScores[position] ?: 0)
        }
    }

    /**
     * Checks if the game is over based on the score limit.
     */
    fun isGameOver(
        cumulativeScores: Map<PlayerPosition, Int>,
        scoreLimit: Int
    ): Boolean {
        return cumulativeScores.values.any { it >= scoreLimit }
    }

    /**
     * Returns the winner (player with lowest cumulative score).
     */
    fun getWinner(cumulativeScores: Map<PlayerPosition, Int>): PlayerPosition {
        return cumulativeScores.minByOrNull { it.value }?.key
            ?: PlayerPosition.SOUTH
    }

    /**
     * Count tricks won by each player from a list of completed tricks.
     */
    fun countTricksWon(completedTricks: List<Trick>): Map<PlayerPosition, Int> {
        return PlayerPosition.allPositions.associateWith { position ->
            completedTricks.count { it.winner == position }
        }
    }
}
