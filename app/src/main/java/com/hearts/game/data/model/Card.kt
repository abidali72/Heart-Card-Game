package com.hearts.game.data.model

/**
 * Represents one of the four card suits.
 */
enum class Suit(val symbol: String, val displayName: String, val color: CardColor) {
    CLUBS("♣", "Clubs", CardColor.BLACK),
    DIAMONDS("♦", "Diamonds", CardColor.RED),
    HEARTS("♥", "Hearts", CardColor.RED),
    SPADES("♠", "Spades", CardColor.BLACK);

    companion object {
        val ordered = listOf(CLUBS, DIAMONDS, SPADES, HEARTS)
    }
}

enum class CardColor { RED, BLACK }

/**
 * Represents card ranks from Two (lowest) to Ace (highest).
 */
enum class Rank(val value: Int, val displayName: String) {
    TWO(2, "2"),
    THREE(3, "3"),
    FOUR(4, "4"),
    FIVE(5, "5"),
    SIX(6, "6"),
    SEVEN(7, "7"),
    EIGHT(8, "8"),
    NINE(9, "9"),
    TEN(10, "10"),
    JACK(11, "J"),
    QUEEN(12, "Q"),
    KING(13, "K"),
    ACE(14, "A");
}

/**
 * A playing card with a suit and rank.
 */
data class Card(
    val suit: Suit,
    val rank: Rank
) : Comparable<Card> {

    val isHeart: Boolean get() = suit == Suit.HEARTS

    val isQueenOfSpades: Boolean get() = suit == Suit.SPADES && rank == Rank.QUEEN

    val isTwoOfClubs: Boolean get() = suit == Suit.CLUBS && rank == Rank.TWO

    /**
     * Point value: Hearts = 1 point each, Queen of Spades = 13 points.
     */
    val pointValue: Int
        get() = when {
            isHeart -> 1
            isQueenOfSpades -> 13
            else -> 0
        }

    val hasPoints: Boolean get() = pointValue > 0

    val displayName: String get() = "${rank.displayName}${suit.symbol}"

    override fun compareTo(other: Card): Int {
        val suitCompare = suit.ordinal.compareTo(other.suit.ordinal)
        return if (suitCompare != 0) suitCompare else rank.value.compareTo(other.rank.value)
    }

    override fun toString(): String = displayName

    companion object {
        val TWO_OF_CLUBS = Card(Suit.CLUBS, Rank.TWO)
        val QUEEN_OF_SPADES = Card(Suit.SPADES, Rank.QUEEN)
    }
}
