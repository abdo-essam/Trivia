package com.qurio.trivia.domain.repository

import com.qurio.trivia.domain.model.Character

interface CharacterRepository {

    suspend fun getSelectedCharacter(): Character

    suspend fun selectCharacter(character: Character)

    suspend fun getAllCharactersWithUnlockStatus(): List<CharacterWithStatus>

    suspend fun isCharacterUnlocked(character: Character): Boolean

    suspend fun purchaseCharacter(character: Character): PurchaseResult

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