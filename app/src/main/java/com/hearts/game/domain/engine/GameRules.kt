package com.hearts.game.domain.engine

import com.hearts.game.data.model.*

/**
 * Strict rule enforcement for the Hearts card game.
 * All validation methods return clear error messages on violations.
 */
object GameRules {

    /**
     * Find the player holding the 2 of Clubs.
     */
    fun findTwoOfClubsHolder(players: Map<PlayerPosition, Player>): PlayerPosition {
        return players.entries.first { (_, player) ->
            player.hasCard(Card.TWO_OF_CLUBS)
        }.key
    }

    /**
     * Returns the list of valid cards a player can play given the current game context.
     */
    fun getValidPlays(
        hand: List<Card>,
        currentTrick: Trick,
        isFirstTrick: Boolean,
        heartsBroken: Boolean
    ): List<Card> {
        if (hand.isEmpty()) return emptyList()

        // First play of first trick: Must play 2 of Clubs
        if (isFirstTrick && currentTrick.isEmpty) {
            return if (hand.contains(Card.TWO_OF_CLUBS)) {
                listOf(Card.TWO_OF_CLUBS)
            } else {
                hand // Shouldn't happen in valid game
            }
        }

        // Must follow suit if possible
        val leadSuit = currentTrick.leadSuit
        if (leadSuit != null) {
            val suitCards = hand.filter { it.suit == leadSuit }
            if (suitCards.isNotEmpty()) {
                return suitCards
            }

            // Void in lead suit — can play anything
            // BUT on first trick, cannot play Hearts or Queen of Spades
            return if (isFirstTrick) {
                val nonPointCards = hand.filter { !it.isHeart && !it.isQueenOfSpades }
                nonPointCards.ifEmpty { hand } // If only point cards, must play them
            } else {
                hand
            }
        }

        // Leading a trick
        return if (!heartsBroken) {
            val nonHearts = hand.filter { !it.isHeart }
            if (nonHearts.isNotEmpty()) {
                nonHearts
            } else {
                // Player has only Hearts — they can lead with one
                hand
            }
        } else {
            hand
        }
    }

    /**
     * Validates whether a specific card can be played.
     * Returns null if valid, or an error message string if invalid.
     */
    fun validatePlay(
        card: Card,
        hand: List<Card>,
        currentTrick: Trick,
        isFirstTrick: Boolean,
        heartsBroken: Boolean
    ): String? {
        val validPlays = getValidPlays(hand, currentTrick, isFirstTrick, heartsBroken)

        if (!validPlays.contains(card)) {
            // Generate specific error message
            if (isFirstTrick && currentTrick.isEmpty && card != Card.TWO_OF_CLUBS) {
                return "You must lead with the 2♣ on the first trick"
            }

            val leadSuit = currentTrick.leadSuit
            if (leadSuit != null && card.suit != leadSuit && hand.any { it.suit == leadSuit }) {
                return "You must follow suit (${leadSuit.displayName})"
            }

            if (isFirstTrick && (card.isHeart || card.isQueenOfSpades)) {
                return "Cannot play Hearts or Q♠ on the first trick"
            }

            if (!heartsBroken && card.isHeart && currentTrick.isEmpty) {
                return "Hearts have not been broken yet"
            }

            return "This card cannot be played right now"
        }

        return null // Valid play
    }

    /**
     * Determines the winner of a completed trick.
     * The winner is the player who played the highest card of the lead suit.
     */
    fun determineTrickWinner(trick: Trick): PlayerPosition {
        require(trick.isComplete) { "Trick is not complete" }
        val leadSuit = trick.leadSuit ?: throw IllegalStateException("No lead suit")

        return trick.plays
            .filter { it.card.suit == leadSuit }
            .maxByOrNull { it.card.rank.value }
            ?.player
            ?: throw IllegalStateException("No cards of lead suit found")
    }

    /**
     * Checks if playing a Heart card should break Hearts.
     */
    fun shouldBreakHearts(card: Card, heartsBroken: Boolean): Boolean {
        return !heartsBroken && card.isHeart
    }

    /**
     * Check if this is a valid set of cards for passing (exactly 3 cards from hand).
     */
    fun validatePassSelection(selectedCards: List<Card>, hand: List<Card>): String? {
        if (selectedCards.size != 3) {
            return "You must select exactly 3 cards to pass"
        }
        if (!hand.containsAll(selectedCards)) {
            return "Selected cards are not in your hand"
        }
        return null
    }
}
