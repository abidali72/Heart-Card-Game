package com.hearts.game.data.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

/**
 * Manages game sounds using SoundPool.
 * In production, audio files would be in res/raw/.
 * This scaffolds the SoundManager with the proper architecture.
 */
class SoundManager(context: Context) {

    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_GAME)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

    // Fallback: AudioManager for system clicks (better than beeps)
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
    
    // SoundPool handles actual card sounds if files exist
    private val soundPool = SoundPool.Builder()
        .setMaxStreams(5)
        .setAudioAttributes(audioAttributes)
        .build()

    private var soundEnabled = true
    private var musicEnabled = true

    // Map to hold sound IDs. If ID is 0, sound failed to load (file missing).
    private val soundIds = mutableMapOf<String, Int>()

    init {
        // Try to load sounds. If files are missing, these will return 0 or fail gracefully.
        // We use string identifiers to map to resource lookups if needed, 
        // but since we don't have R.raw generated yet, we'll try-catch dynamic lookup or just prepare the structure.
        
        // Example: loadSound("card_place") 
    }
    
    private fun loadSound(context: Context, name: String): Int {
        return try {
            val resId = context.resources.getIdentifier(name, "raw", context.packageName)
            if (resId != 0) {
                soundPool.load(context, resId, 1)
            } else 0
        } catch (e: Exception) { 
            0 
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        soundEnabled = enabled
    }

    fun setMusicEnabled(enabled: Boolean) {
        musicEnabled = enabled
    }

    fun playCardPlace() {
        if (!soundEnabled) return
        
        // Try SoundPool first (if user added files)
        // val id = soundIds["card_place"] ?: 0
        // if (id != 0) { soundPool.play(id, 1f, 1f, 1, 0, 1f); return }
        
        // Fallback: System Click
        audioManager.playSoundEffect(android.media.AudioManager.FX_KEY_CLICK, 1.0f)
    }

    fun playCardShuffle() {
        if (!soundEnabled) return
        audioManager.playSoundEffect(android.media.AudioManager.FX_KEYPRESS_SPACEBAR, 0.8f)
    }

    fun playTrickWin() {
        if (!soundEnabled) return
        audioManager.playSoundEffect(android.media.AudioManager.FX_KEYPRESS_RETURN, 1.0f)
    }

    fun playQueenOfSpades() {
        if (!soundEnabled) return
        audioManager.playSoundEffect(android.media.AudioManager.FX_KEYPRESS_DELETE, 1.0f)
    }

    fun playShootTheMoon() {
        if (!soundEnabled) return
        audioManager.playSoundEffect(android.media.AudioManager.FX_FOCUS_NAVIGATION_UP, 1.0f)
    }

    fun startAmbientMusic() {
        if (!musicEnabled) return
        // MediaPlayer logic
    }

    fun stopAmbientMusic() {
        // Stop MediaPlayer
    }

    fun release() {
        soundPool.release()
    }
}
