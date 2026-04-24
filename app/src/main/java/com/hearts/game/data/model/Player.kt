package com.hearts.game.data.model

import androidx.compose.ui.graphics.Color
import com.hearts.game.ui.theme.*


enum class PlayerPosition(val displayName: String) {
    SOUTH("You"),
    WEST("West"),
    NORTH("North"),
    EAST("East");

    fun next(): PlayerPosition = when (this) {
            SOUTH -> WEST
            WEST -> NORTH
            NORTH -> EAST
            EAST -> SOUTH
        }

    companion object {
        val allPositions = listOf(SOUTH, WEST, NORTH, EAST)
        
        fun passTarget(from: PlayerPosition, direction: PassDirection): PlayerPosition {
            return when (direction) {
                PassDirection.LEFT -> from.next()
                PassDirection.RIGHT -> from.next().next().next()
                PassDirection.ACROSS -> from.next().next()
                PassDirection.NONE -> from // Should not happen in passing phase
            }
        }
    }
}

enum class BotPersonality(val displayName: String, val description: String) {
    AGGRESSIVE("Aggressive", "Plays to win tricks"),
    DEFENSIVE("Defensive", "Avoids points at all costs"),
    BALANCED("Balanced", "Adapts to the game state"),
    MOON_HUNTER("Moon Hunter", "High risk, high reward"),
    TRICKSTER("Trickster", "Unpredictable playstyle")
}

enum class PlayerEmotion {
    NEUTRAL,
    HAPPY,      // Won trick
    SAD,        // Lost trick / Bad event
    SHOCKED,    // Received Q Spades
    THINKING,   // AI deciding
    ANGRY       // Hearts broken early?
}

data class BotProfile(
    val name: String,
    val avatarId: Int, // Placeholder for resource ID or index
    val personality: BotPersonality,
    val difficultyBadge: AIDifficulty
)

/**
 * Enhanced Player model with identity and detailed stats.
 */
data class Player(
    val position: PlayerPosition,
    val name: String,
    val isHuman: Boolean = false,
    val hand: List<Card> = emptyList(),
    val avatarId: Int = 0,
    val personality: BotPersonality = BotPersonality.BALANCED,
    val difficulty: AIDifficulty = AIDifficulty.MEDIUM,
    val emotion: PlayerEmotion = PlayerEmotion.NEUTRAL,
    
    // Game Stats
    val roundScore: Int = 0,
    val totalScore: Int = 0,
    val tricksWon: Int = 0,
    val roundTricksWon: Int = 0,
    val moonAttempts: Int = 0,
    val moonSuccesses: Int = 0,
    val gamesWon: Int = 0
) {
    val sortedHand: List<Card>
        get() = hand.sortedWith(compareBy({ it.suit.ordinal }, { it.rank.value }))

    fun hasCard(card: Card): Boolean = hand.contains(card)

    fun hasSuit(suit: Suit): Boolean = hand.any { it.suit == suit }

    fun cardsOfSuit(suit: Suit): List<Card> = hand.filter { it.suit == suit }

    fun hasOnlyHearts(): Boolean = hand.all { it.isHeart }

    val handSize: Int get() = hand.size
    
    val badgeColor: Color
        get() = when (difficulty) {
            AIDifficulty.EASY -> SuccessGreen
            AIDifficulty.MEDIUM -> WarningYellow
            AIDifficulty.HARD -> HeartRed
        }
}
