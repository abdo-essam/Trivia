package com.qurio.trivia.data.repository

import android.util.Log
import com.qurio.trivia.data.database.dao.CharacterDao
import com.qurio.trivia.data.database.dao.UserProgressDao
import com.qurio.trivia.data.mapper.CharacterMapper
import com.qurio.trivia.domain.model.Character
import com.qurio.trivia.domain.repository.CharacterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of CharacterRepository
 * Uses Room database for character storage and unlock status
 */
@Singleton
class CharacterRepositoryImpl @Inject constructor(
    private val characterDao: CharacterDao,
    private val userProgressDao: UserProgressDao,
    private val characterMapper: CharacterMapper
) : CharacterRepository {

    companion object {
        private const val TAG = "CharacterRepositoryImpl"
        private const val DEFAULT_CHARACTER = "rika"
    }

    // ========== Get Characters ==========

    override suspend fun getAllCharacters(): List<Character> = withContext(Dispatchers.IO) {
        try {
            val entities = characterDao.getAllCharacters()
            val selectedName = getSelectedCharacterName()

            characterMapper.toDomainList(entities, selectedName)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting all characters", e)
            emptyList()
        }
    }

    override suspend fun getCharacterByName(name: String): Character? = withContext(Dispatchers.IO) {
        try {
            val entity = characterDao.getCharacterByName(name) ?: return@withContext null
            val isSelected = name == getSelectedCharacterName()

            characterMapper.toDomain(entity, isSelected)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting character by name: $name", e)
            null
        }
    }

    override suspend fun getSelectedCharacter(): Character? = withContext(Dispatchers.IO) {
        try {
            val selectedName = getSelectedCharacterName() ?: DEFAULT_CHARACTER
            getCharacterByName(selectedName)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting selected character", e)
            null
        }
    }



    // ========== Character Selection ==========

    override suspend fun selectCharacter(characterName: String) {
        try {
            // Verify character exists and is unlocked
            if (!isCharacterUnlocked(characterName)) {
                Log.w(TAG, "Attempted to select locked character: $characterName")
                throw IllegalStateException("Cannot select locked character")
            }

            userProgressDao.updateSelectedCharacter(characterName)
            Log.d(TAG, "✓ Character selected: $characterName")
        } catch (e: Exception) {
            Log.e(TAG, "Error selecting character: $characterName", e)
            throw e
        }
    }

    // ========== Character Unlocking ==========

    override suspend fun unlockCharacter(characterName: String)  {
        try {
            characterDao.unlockCharacter(characterName, System.currentTimeMillis())
            Log.d(TAG, "✓ Character unlocked: $characterName")
        } catch (e: Exception) {
            Log.e(TAG, "Error unlocking character: $characterName", e)
            throw e
        }
    }

    override suspend fun isCharacterUnlocked(characterName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val character = characterDao.getCharacterByName(characterName)
            character?.isUnlocked ?: false
        } catch (e: Exception) {
            Log.e(TAG, "Error checking if character is unlocked: $characterName", e)
            false
        }
    }

    // ========== Coins ==========

    override suspend fun getUserCoins(): Int = withContext(Dispatchers.IO) {
        try {
            userProgressDao.getUserProgress()?.totalCoins ?: 0
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user coins", e)
            0
        }
    }

    // ========== Character Purchase ==========

    override suspend fun purchaseCharacter(
        characterName: String,
        cost: Int
    ): CharacterRepository.PurchaseResult = withContext(Dispatchers.IO) {
        try {
            // Get character
            val character = characterDao.getCharacterByName(characterName)
                ?: return@withContext CharacterRepository.PurchaseResult.Error("Character not found")

            // Check if already unlocked
            if (character.isUnlocked) {
                return@withContext CharacterRepository.PurchaseResult.Error("Character already unlocked")
            }

            // Get user progress
            val userProgress = userProgressDao.getUserProgress()
                ?: return@withContext CharacterRepository.PurchaseResult.Error("User not found")

            // Check if user has enough coins
            val currentCoins = userProgress.totalCoins
            if (currentCoins < cost) {
                Log.w(TAG, "Insufficient coins: has $currentCoins, needs $cost")
                return@withContext CharacterRepository.PurchaseResult.InsufficientCoins(
                    currentCoins,
                    cost
                )
            }

            // Deduct coins
            val newCoins = currentCoins - cost
            userProgressDao.updateCoins(newCoins)

            // Unlock character
            unlockCharacter(characterName)

            Log.d(TAG, "✓ Character purchased: $characterName, remaining coins: $newCoins")
            CharacterRepository.PurchaseResult.Success(newCoins)

        } catch (e: Exception) {
            Log.e(TAG, "Error purchasing character: $characterName", e)
            CharacterRepository.PurchaseResult.Error(e.message ?: "Purchase failed")
        }
    }

    // ========== Private Helpers ==========

    private suspend fun getSelectedCharacterName(): String? {
        return try {
            userProgressDao.getUserProgress()?.selectedCharacter
        } catch (e: Exception) {
            Log.e(TAG, "Error getting selected character name", e)
            null
        }
    }
}