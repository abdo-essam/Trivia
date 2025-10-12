package com.qurio.trivia.domain.repository

import com.qurio.trivia.domain.model.Character

/**
 * Repository for character operations
 */
interface CharacterRepository {

    /**
     * Get currently selected character
     */
    suspend fun getSelectedCharacter(): Character

    /**
     * Select a character
     */
    suspend fun selectCharacter(character: Character)

    /**
     * Get all characters with their unlock status
     */
    suspend fun getAllCharactersWithUnlockStatus(): List<CharacterWithStatus>

    /**
     * Get user's current coin balance
     */
    suspend fun getUserCoins(): Int

    /**
     * Check if character is unlocked
     */
    suspend fun isCharacterUnlocked(character: Character): Boolean

    /**
     * Purchase a character with coins
     */
    suspend fun purchaseCharacter(character: Character): PurchaseResult

    /**
     * Data class combining character with unlock status
     */
    data class CharacterWithStatus(
        val character: Character,
        val isUnlocked: Boolean,
        val isSelected: Boolean
    )

    sealed class PurchaseResult {
        data class Success(val remainingCoins: Int) : PurchaseResult()
        data class InsufficientCoins(val currentCoins: Int, val requiredCoins: Int) : PurchaseResult()
        data class Error(val message: String) : PurchaseResult()
    }
}