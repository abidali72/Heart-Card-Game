package com.hearts.game.domain.ai

import com.hearts.game.data.model.*
import kotlin.random.Random

/**
 * Generates unique bot profiles with personalities matched to difficulty.
 */
object BotNameGenerator {

    private val classicNames = listOf(
        "Alex", "Sarah", "Daniel", "Emily", "Michael", "Jessica", "David", "Laura", "Chris", "Anna",
        "James", "Olivia", "Robert", "Sophia", "John", "Emma", "William", "Isabella", "Thomas", "Mia"
    )

    private val strategicNames = listOf(
        "Ace", "Maverick", "Pro", "Shark", "Hunter", "Shadow", "Viper", "Ghost", "Luna", "Raven",
        "Bluff", "Spade", "Heart", "King", "Queen", "Jack", "Joker", "Lucky", "Chance", "Risk"
    )

    private val botPrefixes = listOf(
        "Bot", "AI", "CPU", "Player"
    )

    fun generateProfile(difficulty: AIDifficulty, existingNames: Set<String>): BotProfile {
        var name = generateName(difficulty, existingNames)
        val personality = assignPersonality(difficulty)
        val avatarId = Random.nextInt(0, 10) // Placeholder for 10 avatars

        return BotProfile(
            name = name,
            avatarId = avatarId,
            personality = personality,
            difficultyBadge = difficulty
        )
    }

    private fun generateName(difficulty: AIDifficulty, existingNames: Set<String>): String {
        val pool = when (difficulty) {
            AIDifficulty.EASY -> classicNames
            AIDifficulty.MEDIUM -> classicNames + strategicNames
            AIDifficulty.HARD -> strategicNames
        }

        var name: String
        var attempts = 0
        do {
            name = if (attempts > 10) {
                 "${botPrefixes.random()} ${Random.nextInt(100, 999)}"
            } else {
                pool.random()
            }
            attempts++
        } while (existingNames.contains(name))

        return name
    }

    private fun assignPersonality(difficulty: AIDifficulty): BotPersonality {
        return when (difficulty) {
            AIDifficulty.EASY -> listOf(
                BotPersonality.BALANCED,
                BotPersonality.DEFENSIVE
            ).random()
            
            AIDifficulty.MEDIUM -> listOf(
                BotPersonality.BALANCED,
                BotPersonality.DEFENSIVE,
                BotPersonality.AGGRESSIVE
            ).random()

            AIDifficulty.HARD -> listOf(
                BotPersonality.AGGRESSIVE,
                BotPersonality.MOON_HUNTER,
                BotPersonality.TRICKSTER
            ).random()
        }
    }
}
