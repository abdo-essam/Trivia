package com.qurio.trivia.data.repository

import com.qurio.trivia.data.database.UserProgressDao
import com.qurio.trivia.data.model.Character
import com.qurio.trivia.data.provider.DataProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterRepository @Inject constructor(
    private val userProgressDao: UserProgressDao
) {

    /**
     * Get all available characters
     */
    suspend fun getAllCharacters(): List<Character> {
        return DataProvider.getCharacters()
    }

    /**
     * Get currently selected character
     */
    suspend fun getSelectedCharacter(): String? {
        return userProgressDao.getUserProgress()?.selectedCharacter
    }

    /**
     * Select a character
     */
    suspend fun selectCharacter(characterName: String) {
        userProgressDao.updateSelectedCharacter(characterName)
    }

    /**
     * Get user's current coins
     */
    suspend fun getUserCoins(): Int {
        return userProgressDao.getUserProgress()?.totalCoins ?: 0
    }

    /**
     * Purchase a character
     */
    suspend fun purchaseCharacter(characterName: String, cost: Int): PurchaseResult {
        val userProgress = userProgressDao.getUserProgress()
            ?: return PurchaseResult.Error("User not found")

        return when {
            userProgress.totalCoins < cost -> {
                PurchaseResult.InsufficientCoins(userProgress.totalCoins, cost)
            }
            else -> {
                val newCoins = userProgress.totalCoins - cost
                userProgressDao.updateCoins(newCoins)
                // TODO: Save unlocked character to separate table if needed
                PurchaseResult.Success(newCoins)
            }
        }
    }

    sealed class PurchaseResult {
        data class Success(val remainingCoins: Int) : PurchaseResult()
        data class InsufficientCoins(val currentCoins: Int, val requiredCoins: Int) : PurchaseResult()
        data class Error(val message: String) : PurchaseResult()
    }
}