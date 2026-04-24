package com.hearts.game.domain.ai

import com.hearts.game.data.model.*

/**
 * Medium AI: Balanced player.
 * - Follows standard strategy (avoid points)
 * - Tries to dump high cards
 * - Protects against Queen of Spades slightly
 * - Does NOT count cards or predict deep future
 */
class MediumAI : AIStrategy {

    override fun selectCardToPlay(
        hand: List<Card>,
        validPlays: List<Card>,
        currentTrick: Trick,
        gameState: GameState
    ): Card {
        val leadSuit = currentTrick.leadSuit

        // 1. If leading, try to lead low to avoid winning trick
        if (leadSuit == null) {
            // Don't lead Hearts if possible unless broken (rule enforced elsewhere, but preference here)
            val nonHearts = validPlays.filter { !it.isHeart }
            val candidates = if (nonHearts.isNotEmpty()) nonHearts else validPlays
            
            // Prefer leading low cards
            return candidates.minByOrNull { it.rank.value } ?: candidates.random()
        }

        // 2. If flowing suit:
        // Try to play highest card UNDER the current highest (to save low cards but not win)
        // Or play lowest if we can't beat current highest
        val suitCards = validPlays.filter { it.suit == leadSuit }
        if (suitCards.isNotEmpty()) {
            val winningCard = currentTrick.plays
                .filter { it.card.suit == leadSuit }
                .maxByOrNull { it.card.rank.value }?.card
            
            if (winningCard != null) {
                // Find highest card that is still lower than winning card
                val safeHigh = suitCards.filter { it.rank.value < winningCard.rank.value }
                    .maxByOrNull { it.rank.value }
                
                if (safeHigh != null) return safeHigh
            }
            // Can't go under or first to follow? Play max if last player (and no points)? 
            // Nah, medium AI just plays conservatively: lowest possible
            return suitCards.minByOrNull { it.rank.value } ?: suitCards.random()
        }

        // 3. Void in suit (dumping):
        // Dump Queen of Spades first!
        val qos = validPlays.find { it.isQueenOfSpades }
        if (qos != null) return qos

        // Dump high Hearts
        val hearts = validPlays.filter { it.isHeart }.sortedByDescending { it.rank.value }
        if (hearts.isNotEmpty()) return hearts.first()

        // Dump high cards in general
        return validPlays.maxByOrNull { it.rank.value } ?: validPlays.random()
    }

    override fun selectCardsToPass(hand: List<Card>): List<Card> {
        // Pass Q Spades, A/K Spades, and high Hearts
        return hand.sortedWith(
            compareByDescending<Card> { it.isQueenOfSpades }
                .thenByDescending { it.suit == Suit.SPADES && it.rank.value >= Rank.KING.value }
                .thenByDescending { it.isHeart && it.rank.value >= Rank.JACK.value }
                .thenByDescending { it.rank.value }
        ).take(3)
    }
}
