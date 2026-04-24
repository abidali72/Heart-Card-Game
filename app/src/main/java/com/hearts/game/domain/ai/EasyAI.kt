package com.hearts.game.domain.ai

import com.hearts.game.data.model.*
import kotlin.random.Random

/**
 * Easy AI: Plays mostly random legal moves.
 * Makes potential mistakes like:
 * - Leading with high cards
 * - Breaking hearts early unnecessarily
 * - Missing obvious queen dumps
 */
class EasyAI : AIStrategy {

    override fun selectCardToPlay(
        hand: List<Card>,
        validPlays: List<Card>,
        currentTrick: Trick,
        gameState: GameState
    ): Card {
        // 30% chance to just pick a random legal card (mistake-prone)
        if (Random.nextFloat() < 0.3f) {
            return validPlays.random()
        }

        // 70% chance to try a basic logic (play low)
        return validPlays.minByOrNull { it.rank.value } ?: validPlays.random()
    }

    override fun selectCardsToPass(hand: List<Card>): List<Card> {
        // Randomly select 3 cards
        return hand.shuffled().take(3)
    }
}
