package com.qurio.trivia.domain.repository

import com.qurio.trivia.domain.model.Character

/**
 * Repository interface for character operations
 * Part of domain layer - defines what operations are available
 */
interface CharacterRepository {

    /**
     * Get all characters with their lock status
     */
    suspend fun getAllCharacters(): List<Character>

    /**
     * Get a specific character by name
     */
    suspend fun getCharacterByName(name: String): Character?

    /**
     * Get currently selected character
     */
    suspend fun getSelectedCharacter(): Character?

    /**
     * Select a character as active
     */
    suspend fun selectCharacter(characterName: String)

    /**
     * Get user's current coin balance
     */
    suspend fun getUserCoins(): Int

    /**
     * Unlock a character by spending coins
     */
    suspend fun unlockCharacter(characterName: String)

    /**
     * Check if character is unlocked
     */
    suspend fun isCharacterUnlocked(characterName: String): Boolean

    /**
     * Purchase a character with coins
     */
    suspend fun purchaseCharacter(characterName: String, cost: Int): PurchaseResult

    sealed class PurchaseResult {
        data class Success(val remainingCoins: Int) : PurchaseResult()
        data class InsufficientCoins(val currentCoins: Int, val requiredCoins: Int) : PurchaseResult()
        data class Error(val message: String) : PurchaseResult()
    }
}