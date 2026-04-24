package com.hearts.game.data.multiplayer

import com.hearts.game.data.model.*

/**
 * Multiplayer manager interface.
 * Provides the architecture for online, private room, and local pass-and-play modes.
 */
interface MultiplayerManager {
    suspend fun createRoom(config: GameConfig): String  // Returns room code
    suspend fun joinRoom(roomCode: String): Boolean
    suspend fun leaveRoom()
    suspend fun sendPlay(card: Card)
    suspend fun sendPassCards(cards: List<Card>)
    fun onGameStateUpdate(callback: (GameState) -> Unit)
    fun onPlayerJoined(callback: (PlayerPosition) -> Unit)
    fun onPlayerLeft(callback: (PlayerPosition) -> Unit)
    fun isConnected(): Boolean
}

/**
 * Local pass-and-play multiplayer mode (functional).
 * All players share the same device.
 */
class LocalMultiplayerManager : MultiplayerManager {

    private var currentRoom: String? = null
    private var gameStateCallback: ((GameState) -> Unit)? = null

    override suspend fun createRoom(config: GameConfig): String {
        val code = (100000..999999).random().toString()
        currentRoom = code
        return code
    }

    override suspend fun joinRoom(roomCode: String): Boolean {
        currentRoom = roomCode
        return true
    }

    override suspend fun leaveRoom() {
        currentRoom = null
    }

    override suspend fun sendPlay(card: Card) {
        // In local mode, plays are handled directly by the game engine
    }

    override suspend fun sendPassCards(cards: List<Card>) {
        // In local mode, passes are handled directly by the game engine
    }

    override fun onGameStateUpdate(callback: (GameState) -> Unit) {
        gameStateCallback = callback
    }

    override fun onPlayerJoined(callback: (PlayerPosition) -> Unit) {
        // N/A for local play
    }

    override fun onPlayerLeft(callback: (PlayerPosition) -> Unit) {
        // N/A for local play
    }

    override fun isConnected(): Boolean = currentRoom != null
}

/**
 * Online multiplayer stub — ready for Firebase/WebSocket backend integration.
 */
class OnlineMultiplayerManager : MultiplayerManager {
    override suspend fun createRoom(config: GameConfig): String {
        TODO("Integrate with server backend (Firebase/WebSocket)")
    }

    override suspend fun joinRoom(roomCode: String): Boolean {
        TODO("Integrate with server backend")
    }

    override suspend fun leaveRoom() {
        TODO("Integrate with server backend")
    }

    override suspend fun sendPlay(card: Card) {
        TODO("Integrate with server backend")
    }

    override suspend fun sendPassCards(cards: List<Card>) {
        TODO("Integrate with server backend")
    }

    override fun onGameStateUpdate(callback: (GameState) -> Unit) {
        TODO("Integrate with server backend")
    }

    override fun onPlayerJoined(callback: (PlayerPosition) -> Unit) {
        TODO("Integrate with server backend")
    }

    override fun onPlayerLeft(callback: (PlayerPosition) -> Unit) {
        TODO("Integrate with server backend")
    }

    override fun isConnected(): Boolean = false
}
