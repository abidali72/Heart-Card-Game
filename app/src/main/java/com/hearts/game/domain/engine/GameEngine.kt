package com.hearts.game.domain.engine

import com.hearts.game.data.model.*
import com.hearts.game.domain.ai.BotNameGenerator

/**
 * Main game orchestrator implementing the full state machine:
 * Init → Shuffle → Deal → Pass Phase → Trick Loop → Score Calculation → Round End → Game End
 *
 * This engine is deterministic and UI-independent.
 */
class GameEngine(
    private val config: GameConfig = GameConfig()
) {
    private var _state: GameState = GameState(config = config)
    val state: GameState get() = _state

    // Track which tricks each player has won this round (for scoring)
    private val roundTrickResults = mutableMapOf<PlayerPosition, MutableList<Trick>>()

    /**
     * Start a brand-new game.
     */
    fun startNewGame(): GameState {
        val playerNames = mutableSetOf<String>("You")
        
        _state = GameState(
            phase = GamePhase.Waiting,
            roundNumber = 1,
            config = config,
            players = PlayerPosition.allPositions.associateWith { pos ->
                if (pos == PlayerPosition.SOUTH) {
                    Player(
                        position = pos,
                        name = "You",
                        isHuman = true,
                        avatarId = 0
                    )
                } else {
                    val profile = BotNameGenerator.generateProfile(config.aiDifficulty, playerNames)
                    playerNames.add(profile.name)
                    Player(
                        position = pos,
                        name = profile.name,
                        isHuman = false,
                        avatarId = profile.avatarId,
                        personality = profile.personality,
                        difficulty = config.aiDifficulty
                    )
                }
            }
        )
        return startNewRound()
    }

    /**
     * Begin a new round: shuffle, deal, determine pass direction.
     */
    fun startNewRound(): GameState {
        // Reset round trick results
        roundTrickResults.clear()
        PlayerPosition.allPositions.forEach {
            roundTrickResults[it] = mutableListOf()
        }

        // Deal cards
        val hands = Deck.deal()
        val passDir = PassDirection.forRound(_state.roundNumber)

        // Update players with new hands, reset round stats
        val updatedPlayers = _state.players.mapValues { (pos, player) ->
            player.copy(
                hand = hands[pos] ?: emptyList(),
                roundScore = 0,
                roundTricksWon = 0
            )
        }

        _state = _state.copy(
            players = updatedPlayers,
            passDirection = passDir,
            heartsBroken = false,
            currentTrick = Trick(trickNumber = 1),
            completedTricks = emptyList(),
            trickHistory = emptyList(),
            selectedCards = emptyList(),
            errorMessage = null
        )

        // Determine phase: passing or playing
        _state = if (passDir == PassDirection.NONE) {
            // No pass round — go straight to playing
            val starter = GameRules.findTwoOfClubsHolder(_state.players)
            _state.copy(
                phase = GamePhase.Playing(
                    currentPlayer = starter,
                    trickNumber = 1
                )
            )
        } else {
            _state.copy(phase = GamePhase.Passing(passDir))
        }

        return _state
    }

    /**
     * Toggle a card in the pass selection.
     */
    fun toggleCardSelection(card: Card): GameState {
        val current = _state.selectedCards.toMutableList()
        if (current.contains(card)) {
            current.remove(card)
        } else if (current.size < 3) {
            current.add(card)
        }
        _state = _state.copy(selectedCards = current, errorMessage = null)
        return _state
    }

    /**
     * Execute the card passing for all players.
     * humanSelection: the 3 cards the human selected to pass.
     * aiSelections: function to get AI pass selections.
     */
    fun executePass(
        humanSelection: List<Card>,
        aiPassSelector: (Player, GameState) -> List<Card>
    ): GameState {
        val error = GameRules.validatePassSelection(
            humanSelection,
            _state.getPlayer(PlayerPosition.SOUTH).hand
        )
        if (error != null) {
            _state = _state.copy(errorMessage = error)
            return _state
        }

        val direction = _state.passDirection
        val allSelections = mutableMapOf<PlayerPosition, List<Card>>()
        allSelections[PlayerPosition.SOUTH] = humanSelection

        // Get AI selections
        for (pos in listOf(PlayerPosition.WEST, PlayerPosition.NORTH, PlayerPosition.EAST)) {
            val player = _state.getPlayer(pos)
            allSelections[pos] = aiPassSelector(player, _state)
        }

        // Execute passing: remove selected cards from senders, add to receivers
        val newHands = _state.players.mapValues { (pos, player) ->
            player.hand.toMutableList()
        }.toMutableMap()

        for ((fromPos, cards) in allSelections) {
            val toPos = PlayerPosition.passTarget(fromPos, direction)
            // Remove from sender
            newHands[fromPos] = newHands[fromPos]!!.apply { removeAll(cards.toSet()) }
            // Add to receiver
            newHands[toPos] = newHands[toPos]!!.apply { addAll(cards) }
        }

        // Update players and sort hands
        val updatedPlayers = _state.players.mapValues { (pos, player) ->
            player.copy(
                hand = newHands[pos]!!.sortedWith(compareBy({ it.suit.ordinal }, { it.rank.value }))
            )
        }

        // Find who has 2 of Clubs to start
        val starter = GameRules.findTwoOfClubsHolder(updatedPlayers)

        _state = _state.copy(
            players = updatedPlayers,
            selectedCards = emptyList(),
            errorMessage = null,
            phase = GamePhase.Playing(currentPlayer = starter, trickNumber = 1)
        )

        return _state
    }

    /**
     * Play a card from the given player's hand.
     */
    fun playCard(position: PlayerPosition, card: Card): GameState {
        val player = _state.getPlayer(position)
        val isFirstTrick = _state.completedTricks.isEmpty() && _state.currentTrick.plays.isEmpty()
            || (_state.completedTricks.isEmpty() && _state.currentTrick.trickNumber == 1)

        // Validate the play
        val error = GameRules.validatePlay(
            card = card,
            hand = player.hand,
            currentTrick = _state.currentTrick,
            isFirstTrick = _state.currentTrick.trickNumber == 1,
            heartsBroken = _state.heartsBroken
        )

        if (error != null) {
            _state = _state.copy(errorMessage = error)
            return _state
        }

        // Check if hearts are broken
        val newHeartsBroken = _state.heartsBroken ||
            GameRules.shouldBreakHearts(card, _state.heartsBroken)

        // Remove card from player's hand
        val updatedPlayer = player.copy(hand = player.hand - card)
        val updatedPlayers = _state.players.toMutableMap()
        updatedPlayers[position] = updatedPlayer

        // Add card to current trick
        val updatedTrick = _state.currentTrick.addPlay(TrickPlay(position, card))

        _state = _state.copy(
            players = updatedPlayers,
            currentTrick = updatedTrick,
            heartsBroken = newHeartsBroken,
            errorMessage = null
        )

        // Check if trick is complete
        if (updatedTrick.isComplete) {
            completeTrick(updatedTrick)
        } else {
            // Move to next player
            val nextPlayer = position.next()
            _state = _state.copy(
                phase = GamePhase.Playing(
                    currentPlayer = nextPlayer,
                    trickNumber = updatedTrick.trickNumber
                )
            )
        }

        return _state
    }

    /**
     * Process a completed trick: determine winner, update scores, advance state.
     */
    private fun completeTrick(trick: Trick) {
        val winner = GameRules.determineTrickWinner(trick)
        val completedTrick = trick.copy(winner = winner)

        // Track trick result for scoring
        roundTrickResults.getOrPut(winner) { mutableListOf() }.add(completedTrick)

        val completedTricks = _state.completedTricks + completedTrick
        val trickHistory = _state.trickHistory + completedTrick

        // Calculate points for this trick
        val trickPoints = ScoringEngine.calculateTrickPoints(completedTrick)

        // Update trick winner stats and LIVE round score
        val updatedPlayers = _state.players.toMutableMap()
        val winnerPlayer = updatedPlayers[winner]!!
        updatedPlayers[winner] = winnerPlayer.copy(
            roundTricksWon = winnerPlayer.roundTricksWon + 1,
            tricksWon = winnerPlayer.tricksWon + 1,
            roundScore = winnerPlayer.roundScore + trickPoints // Add points immediately
        )

        _state = _state.copy(
            players = updatedPlayers,
            completedTricks = completedTricks,
            trickHistory = trickHistory,
            currentTrick = completedTrick,
            phase = GamePhase.TrickComplete(trick = completedTrick, winner = winner)
        )
    }

    /**
     * Called after TrickComplete animation is done.
     * Starts next trick or completes the round.
     */
    fun advanceAfterTrick(): GameState {
        val completedTricks = _state.completedTricks

        if (completedTricks.size >= 13) {
            // Round is over — calculate scores
            return completeRound()
        }

        // Start next trick — winner of last trick leads
        val lastWinner = completedTricks.last().winner!!
        val nextTrickNumber = completedTricks.size + 1

        _state = _state.copy(
            currentTrick = Trick(trickNumber = nextTrickNumber),
            phase = GamePhase.Playing(
                currentPlayer = lastWinner,
                trickNumber = nextTrickNumber
            )
        )

        return _state
    }

    /**
     * Calculate round scores and determine if game is over.
     */
    private fun completeRound(): GameState {
        val (roundScores, moonShooter) = ScoringEngine.finalizeRoundScores(roundTrickResults)

        // Update cumulative scores
        val cumulativeScores = _state.players.mapValues { (pos, player) ->
            player.totalScore + (roundScores[pos] ?: 0)
        }

        // Update player stats
        val updatedPlayers = _state.players.mapValues { (pos, player) ->
            player.copy(
                roundScore = roundScores[pos] ?: 0,
                totalScore = cumulativeScores[pos] ?: 0,
                moonAttempts = if (moonShooter == pos) player.moonAttempts + 1 else player.moonAttempts,
                moonSuccesses = if (moonShooter == pos) player.moonSuccesses + 1 else player.moonSuccesses
            )
        }

        _state = _state.copy(
            players = updatedPlayers,
            phase = GamePhase.RoundComplete(
                roundScores = roundScores,
                cumulativeScores = cumulativeScores,
                moonShooter = moonShooter
            )
        )

        return _state
    }

    /**
     * After round-complete screen, either start next round or end game.
     */
    fun advanceAfterRound(): GameState {
        val cumulativeScores = _state.players.mapValues { it.value.totalScore }

        if (ScoringEngine.isGameOver(cumulativeScores, config.scoreLimit)) {
            val winner = ScoringEngine.getWinner(cumulativeScores)

            // Update games won
            val updatedPlayers = _state.players.mapValues { (pos, player) ->
                if (pos == winner) player.copy(gamesWon = player.gamesWon + 1) else player
            }

            _state = _state.copy(
                players = updatedPlayers,
                phase = GamePhase.GameOver(
                    winner = winner,
                    finalScores = cumulativeScores
                )
            )
            return _state
        }

        // Next round
        _state = _state.copy(roundNumber = _state.roundNumber + 1)
        return startNewRound()
    }

    /**
     * Get error message and clear it.
     */
    fun consumeError(): String? {
        val error = _state.errorMessage
        _state = _state.copy(errorMessage = null)
        return error
    }

    /**
     * Update game configuration dynamically.
     */
    fun updateConfig(newConfig: GameConfig) {
        _state = _state.copy(config = newConfig)
    }
}
