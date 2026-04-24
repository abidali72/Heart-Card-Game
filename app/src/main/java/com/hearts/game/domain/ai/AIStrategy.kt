package com.hearts.game.domain.ai

import com.hearts.game.data.model.*

/**
 * Interface for AI card selection strategies.
 */
interface AIStrategy {
    /**
     * Select a card to play from the valid plays.
     */
    fun selectCardToPlay(
        hand: List<Card>,
        validPlays: List<Card>,
        currentTrick: Trick,
        gameState: GameState
    ): Card

    /**
     * Select 3 cards to pass.
     */
    fun selectCardsToPass(hand: List<Card>): List<Card>
}
