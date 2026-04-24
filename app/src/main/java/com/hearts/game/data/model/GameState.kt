package com.hearts.game.data.model

/**
 * Card passing direction, cycling every 4 rounds.
 */
enum class PassDirection(val displayName: String, val description: String) {
    LEFT("Pass Left", "Pass 3 cards to the player on your left"),
    RIGHT("Pass Right", "Pass 3 cards to the player on your right"),
    ACROSS("Pass Across", "Pass 3 cards to the player across from you"),
    NONE("No Pass", "Keep your cards this round");

    companion object {
        /**
         * Returns the pass direction for a given round number (1-indexed).
         */
        fun forRound(roundNumber: Int): PassDirection {
            return when ((roundNumber - 1) % 4) {
                0 -> LEFT
                1 -> RIGHT
                2 -> ACROSS
                3 -> NONE
                else -> NONE
            }
        }
    }
}

/**
 * Represents a single play within a trick (who played what card).
 */
data class TrickPlay(
    val player: PlayerPosition,
    val card: Card
)

/**
 * Represents one trick — a collection of 4 plays.
 */
data class Trick(
    val trickNumber: Int,
    val plays: List<TrickPlay> = emptyList(),
    val leadSuit: Suit? = null,
    val winner: PlayerPosition? = null
) {
    val isComplete: Boolean get() = plays.size == 4

    val isEmpty: Boolean get() = plays.isEmpty()

    val currentPlays: Int get() = plays.size

    val points: Int get() = plays.sumOf { it.card.pointValue }

    fun addPlay(play: TrickPlay): Trick {
        val newPlays = plays + play
        val newLeadSuit = if (plays.isEmpty()) play.card.suit else leadSuit
        return copy(plays = newPlays, leadSuit = newLeadSuit)
    }

    fun getCards(): List<Card> = plays.map { it.card }

    fun hasCard(card: Card): Boolean = plays.any { it.card == card }

    fun getPlayByPosition(position: PlayerPosition): TrickPlay? =
        plays.find { it.player == position }
}

/**
 * Game configuration options.
 */
data class GameConfig(
    val scoreLimit: Int = 100,
    val aiDifficulty: AIDifficulty = AIDifficulty.MEDIUM,
    val gameSpeed: GameSpeed = GameSpeed.NORMAL,
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val cardTheme: Int = 0,
    val tableTheme: Int = 0
)

enum class AIDifficulty(val displayName: String) {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard")
}

enum class GameSpeed(val displayName: String, val delayMs: Long, val animStiffness: Float, val dealDurationMs: Int) {
    SLOW("Slow", 1200L, 300f, 300),
    NORMAL("Normal", 800L, 1500f, 150),
    FAST("Fast", 400L, 5000f, 50)
}

/**
 * Sealed class hierarchy representing all phases of the game.
 */
sealed class GamePhase {
    data object Waiting : GamePhase()
    data object Dealing : GamePhase()
    data class Passing(val direction: PassDirection) : GamePhase()
    data class Playing(
        val currentPlayer: PlayerPosition,
        val trickNumber: Int
    ) : GamePhase()
    data class TrickComplete(
        val trick: Trick,
        val winner: PlayerPosition
    ) : GamePhase()
    data class RoundComplete(
        val roundScores: Map<PlayerPosition, Int>,
        val cumulativeScores: Map<PlayerPosition, Int>,
        val moonShooter: PlayerPosition? = null
    ) : GamePhase()
    data class GameOver(
        val winner: PlayerPosition,
        val finalScores: Map<PlayerPosition, Int>
    ) : GamePhase()
}

/**
 * Complete snapshot of the game state at any point.
 */
data class GameState(
    val phase: GamePhase = GamePhase.Waiting,
    val players: Map<PlayerPosition, Player> = emptyMap(),
    val currentTrick: Trick = Trick(trickNumber = 1),
    val completedTricks: List<Trick> = emptyList(),
    val heartsBroken: Boolean = false,
    val roundNumber: Int = 1,
    val passDirection: PassDirection = PassDirection.LEFT,
    val config: GameConfig = GameConfig(),
    val selectedCards: List<Card> = emptyList(),
    val errorMessage: String? = null,
    val trickHistory: List<Trick> = emptyList()
) {
    val humanPlayer: Player? get() = players[PlayerPosition.SOUTH]

    val currentPlayerPosition: PlayerPosition?
        get() = (phase as? GamePhase.Playing)?.currentPlayer

    val isHumanTurn: Boolean
        get() = currentPlayerPosition == PlayerPosition.SOUTH

    val isPassingPhase: Boolean
        get() = phase is GamePhase.Passing

    val trickCount: Int
        get() = completedTricks.size

    fun getPlayer(position: PlayerPosition): Player =
        players[position] ?: throw IllegalStateException("No player at $position")
}
