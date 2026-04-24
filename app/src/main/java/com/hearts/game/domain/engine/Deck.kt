package com.hearts.game.domain.engine

import com.hearts.game.data.model.*
import java.security.SecureRandom

/**
 * Manages deck creation, shuffling, and dealing.
 */
object Deck {

    private val secureRandom = SecureRandom()

    /**
     * Creates a standard 52-card deck.
     */
    fun createStandardDeck(): List<Card> {
        return Suit.entries.flatMap { suit ->
            Rank.entries.map { rank -> Card(suit, rank) }
        }
    }

    /**
     * Shuffles a deck using SecureRandom for fairness.
     */
    fun shuffle(deck: List<Card>): List<Card> {
        val mutable = deck.toMutableList()
        for (i in mutable.size - 1 downTo 1) {
            val j = secureRandom.nextInt(i + 1)
            val temp = mutable[i]
            mutable[i] = mutable[j]
            mutable[j] = temp
        }
        return mutable.toList()
    }

    /**
     * Deals 13 cards to each of 4 players.
     * Returns a map of PlayerPosition to their hand.
     */
    fun deal(): Map<PlayerPosition, List<Card>> {
        val shuffled = shuffle(createStandardDeck())
        return mapOf(
            PlayerPosition.SOUTH to shuffled.subList(0, 13).sortedWith(cardComparator),
            PlayerPosition.WEST to shuffled.subList(13, 26).sortedWith(cardComparator),
            PlayerPosition.NORTH to shuffled.subList(26, 39).sortedWith(cardComparator),
            PlayerPosition.EAST to shuffled.subList(39, 52).sortedWith(cardComparator)
        )
    }

    private val cardComparator = compareBy<Card>({ it.suit.ordinal }, { it.rank.value })
}
