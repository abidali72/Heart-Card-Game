package com.hearts.game.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hearts.game.data.model.*
import com.hearts.game.data.audio.SoundManager
import com.hearts.game.domain.ai.*
import com.hearts.game.domain.engine.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.random.Random

/**
 * Main ViewModel orchestrating game state, AI turns, and user interactions.
 */
class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _showError = MutableStateFlow<String?>(null)
    val showError: StateFlow<String?> = _showError.asStateFlow()

    private val _showMoonAnimation = MutableStateFlow(false)
    val showMoonAnimation: StateFlow<Boolean> = _showMoonAnimation.asStateFlow()

    private val _isAnimating = MutableStateFlow(false)
    val isAnimating: StateFlow<Boolean> = _isAnimating.asStateFlow()

    private lateinit var engine: GameEngine
    private var aiStrategy: AIStrategy = MediumAI()
    private val cardTracker = CardTracker()
    private var aiJob: Job? = null
    
    // Sound Manager
    private val soundManager = SoundManager(application.applicationContext)

    fun startNewGame(config: GameConfig = GameConfig()) {
        aiJob?.cancel()
        engine = GameEngine(config)

        // Set AI strategy based on difficulty
        aiStrategy = when (config.aiDifficulty) {
            AIDifficulty.EASY -> EasyAI()
            AIDifficulty.MEDIUM -> MediumAI()
            AIDifficulty.HARD -> HardAI(cardTracker)
        }
        cardTracker.reset()

        val state = engine.startNewGame()
        _gameState.value = state

        // If no passing phase, check if AI goes first
        if (state.phase is GamePhase.Playing) {
            checkAndPlayAI()
        }
        
        soundManager.playCardShuffle()
    }

    /**
     * Toggle a card in the pass selection.
     */
    fun togglePassCard(card: Card) {
        val state = engine.toggleCardSelection(card)
        _gameState.value = state
        soundManager.playCardPlace()
    }

    /**
     * Confirm passing selected cards.
     */
    fun confirmPass() {
        val selectedCards = _gameState.value.selectedCards
        if (selectedCards.size != 3) {
            _showError.value = "Select exactly 3 cards to pass"
            return
        }

        val state = engine.executePass(selectedCards) { player, gameState ->
            aiStrategy.selectCardsToPass(player.hand)
        }

        _gameState.value = state
        soundManager.playCardShuffle() // Passing cards sound

        if (state.errorMessage != null) {
            _showError.value = state.errorMessage
        } else {
            // Start playing - check if AI should go first
            checkAndPlayAI()
        }
    }

    /**
     * Human plays a card.
     */
    fun playCard(card: Card) {
        if (_isAnimating.value) return

        val state = engine.playCard(PlayerPosition.SOUTH, card)
        _gameState.value = state
        
        soundManager.playCardPlace()

        if (state.errorMessage != null) {
            _showError.value = state.errorMessage
            viewModelScope.launch {
                delay(2000)
                _showError.value = null
            }
            return
        }

        // Track the play
        cardTracker.recordPlay(PlayerPosition.SOUTH, card, state.currentTrick.leadSuit)

        // If trick is complete, handle it
        if (state.phase is GamePhase.TrickComplete) {
            handleTrickComplete()
        } else {
            // Next player (AI)
            checkAndPlayAI()
        }
    }

    private fun handleTrickComplete() {
        _isAnimating.value = true
        
        // Determine emotion for winner
        val state = _gameState.value
        val trick = (state.phase as? GamePhase.TrickComplete)?.trick
        val winner = (state.phase as? GamePhase.TrickComplete)?.winner
        
        if (trick != null && winner != null) {
            val qosPlayed = trick.plays.any { it.card.isQueenOfSpades }
            val points = trick.points
            
            if (qosPlayed) {
                // Initial shock/sadness for taking Q Spades
                setPlayerEmotion(winner, PlayerEmotion.SHOCKED, 3000)
                soundManager.playQueenOfSpades()
            } else if (points > 0) {
                // Taking points is generally bad (unless shooting moon - hard to know intent here simply)
                // Let's say SAD if they took points
                setPlayerEmotion(winner, PlayerEmotion.SAD, 2000)
                soundManager.playTrickWin()
            } else {
                // Clean trick - Happy!
                setPlayerEmotion(winner, PlayerEmotion.HAPPY, 2000)
                soundManager.playTrickWin()
            }
        }
        
        aiJob = viewModelScope.launch {
            delay(getAnimDelay()) // Show completed trick
            val nextState = engine.advanceAfterTrick()
            _gameState.value = nextState
            _isAnimating.value = false
 
            when (nextState.phase) {
                is GamePhase.RoundComplete -> handleRoundComplete(nextState.phase)
                is GamePhase.Playing -> checkAndPlayAI()
                else -> {}
            }
        }
    }

    private fun handleRoundComplete(phase: GamePhase.RoundComplete) {
        if (phase.moonShooter != null) {
            _showMoonAnimation.value = true
            soundManager.playShootTheMoon()
            viewModelScope.launch {
                delay(3000) // Show moon celebration
                _showMoonAnimation.value = false
            }
        }
    }
    
    private fun setPlayerEmotion(position: PlayerPosition, emotion: PlayerEmotion, duration: Long = 2000) {
        val currentPlayers = _gameState.value.players.toMutableMap()
        val player = currentPlayers[position] ?: return
        currentPlayers[position] = player.copy(emotion = emotion)
        _gameState.value = _gameState.value.copy(players = currentPlayers)
        
        viewModelScope.launch {
            delay(duration)
            // Reset to neutral if still same emotion
            val latestPlayers = _gameState.value.players.toMutableMap()
            val latestPlayer = latestPlayers[position]
            if (latestPlayer?.emotion == emotion) {
                latestPlayers[position] = latestPlayer.copy(emotion = PlayerEmotion.NEUTRAL)
                _gameState.value = _gameState.value.copy(players = latestPlayers)
            }
        }
    }

    /**
     * Continue to next round after viewing scores.
     */
    fun continueAfterRound() {
        val state = engine.advanceAfterRound()
        _gameState.value = state
        cardTracker.reset()

        if (state.phase is GamePhase.Playing) {
            checkAndPlayAI()
        }
        soundManager.playCardShuffle()
    }

    /**
     * Restart the game from scratch.
     */
    fun restartGame() {
        startNewGame(_gameState.value.config)
    }

    /**
     * Check if it's an AI player's turn and play automatically.
     */
    private fun checkAndPlayAI() {
        val phase = _gameState.value.phase
        if (phase !is GamePhase.Playing) return
        if (phase.currentPlayer == PlayerPosition.SOUTH) return // Human turn

        aiJob?.cancel()
        aiJob = viewModelScope.launch {
            delay(getAnimDelay() / 2) // Brief thinking pause
            playAITurn(phase.currentPlayer)
        }
    }

    /**
     * Get thinking delay based on difficulty.
     */
    private fun getAIThinkingDelay(difficulty: AIDifficulty): Long {
        return when (difficulty) {
            AIDifficulty.EASY -> Random.nextLong(1500, 2500)
            AIDifficulty.MEDIUM -> Random.nextLong(1000, 2000)
            AIDifficulty.HARD -> Random.nextLong(500, 1200)
        }
    }

    private suspend fun playAITurn(position: PlayerPosition) {
        val state = _gameState.value
        val player = state.getPlayer(position)

        // 1. Thinking phase
        setPlayerEmotion(position, PlayerEmotion.THINKING, duration = 1000)
        val thinkingDelay = getAIThinkingDelay(state.config.aiDifficulty)
        delay(thinkingDelay)

        val validPlays = GameRules.getValidPlays(
            hand = player.hand,
            currentTrick = state.currentTrick,
            isFirstTrick = state.currentTrick.trickNumber == 1,
            heartsBroken = state.heartsBroken
        )

        if (validPlays.isEmpty()) return

        val card = aiStrategy.selectCardToPlay(
            hand = player.hand,
            validPlays = validPlays,
            currentTrick = state.currentTrick,
            gameState = state
        )

        // Track the play
        cardTracker.recordPlay(position, card, state.currentTrick.leadSuit)

        val newState = engine.playCard(position, card)
        _gameState.value = newState
        soundManager.playCardPlace()

        if (newState.phase is GamePhase.TrickComplete) {
            handleTrickComplete()
        } else {
            // Continue to next player
            delay(200) // Brief pause between AI plays
            checkAndPlayAI()
        }
    }

    private fun getAnimDelay(): Long {
        return _gameState.value.config.gameSpeed.delayMs
    }

    fun clearError() {
        _showError.value = null
    }

    fun updateConfig(newConfig: GameConfig) {
        if (_gameState.value.config == newConfig) return
        
        // Preserve current state but update config
        val updatedState = _gameState.value.copy(config = newConfig)
        _gameState.value = updatedState
        
        // Also update engine config if possible
        if (::engine.isInitialized) {
            engine.updateConfig(newConfig)
        }
        
        // Update AI Strategy if difficulty changed
        if (_gameState.value.config.aiDifficulty != newConfig.aiDifficulty) {
            aiStrategy = when (newConfig.aiDifficulty) {
                AIDifficulty.EASY -> EasyAI()
                AIDifficulty.MEDIUM -> MediumAI()
                AIDifficulty.HARD -> HardAI(cardTracker)
            }
        }
        
        // Update sound settings
        soundManager.setSoundEnabled(newConfig.soundEnabled)
        soundManager.setMusicEnabled(newConfig.musicEnabled)
    }

    override fun onCleared() {
        super.onCleared()
        aiJob?.cancel()
        soundManager.release()
    }
}
