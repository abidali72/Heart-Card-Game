package com.hearts.game.domain.ai

import com.hearts.game.data.model.*

/**
 * Tracks all played cards throughout a round to enable advanced AI decision-making.
 */
class CardTracker {
    private val playedCards = mutableSetOf<Card>()
    private val playerVoids = mutableMapOf<PlayerPosition, MutableSet<Suit>>()

    init {
        PlayerPosition.allPositions.forEach { playerVoids[it] = mutableSetOf() }
    }

    fun reset() {
        playedCards.clear()
        playerVoids.clear()
        PlayerPosition.allPositions.forEach { playerVoids[it] = mutableSetOf() }
    }

    fun recordPlay(position: PlayerPosition, card: Card, leadSuit: Suit?) {
        playedCards.add(card)

        // If player didn't follow suit, they're void in that suit
        if (leadSuit != null && card.suit != leadSuit) {
            playerVoids[position]?.add(leadSuit)
        }
    }

    fun isPlayed(card: Card): Boolean = playedCards.contains(card)

    fun isPlayerVoidIn(position: PlayerPosition, suit: Suit): Boolean =
        playerVoids[position]?.contains(suit) == true

    /**
     * Get remaining cards of a suit that haven't been played.
     */
    fun remainingCardsOfSuit(suit: Suit): List<Card> {
        return Rank.entries.map { Card(suit, it) }.filter { !playedCards.contains(it) }
    }

    /**
     * Count how many cards of a suit are still out.
     */
    fun remainingCountOfSuit(suit: Suit): Int = remainingCardsOfSuit(suit).size

    /**
     * Is the Queen of Spades still unplayed?
     */
    fun isQueenOfSpadesOut(): Boolean = !playedCards.contains(Card.QUEEN_OF_SPADES)

    /**
     * Get remaining high cards (above a threshold) for a suit.
     */
    fun remainingHighCards(suit: Suit, minRank: Rank = Rank.JACK): List<Card> {
        return remainingCardsOfSuit(suit).filter { it.rank.value >= minRank.value }
    }

    /**
     * Estimate probability that a specific player has a specific card.
     */
    fun probabilityPlayerHasCard(
        position: PlayerPosition,
        card: Card,
        totalPlayersWhoCouldHaveIt: Int = 3
    ): Float {
        if (isPlayed(card)) return 0f
        if (isPlayerVoidIn(position, card.suit)) return 0f
        return 1f / totalPlayersWhoCouldHaveIt
    }

    /**
     * Check total points still available in unplayed cards.
     */
    fun remainingPoints(): Int {
        return Suit.entries.flatMap { suit ->
            Rank.entries.map { rank -> Card(suit, rank) }
        }.filter { !playedCards.contains(it) }.sumOf { it.pointValue }
    }

    /**
     * Get points collected by a player so far (from trick history).
     */
    fun pointsCollectedBy(
        position: PlayerPosition,
        completedTricks: List<Trick>
    ): Int {
        return completedTricks.filter { it.winner == position }
            .sumOf { trick -> trick.plays.sumOf { it.card.pointValue } }
    }
}
