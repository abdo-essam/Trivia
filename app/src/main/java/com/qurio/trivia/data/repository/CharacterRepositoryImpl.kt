package com.qurio.trivia.data.repository

import com.qurio.trivia.data.database.dao.CharacterDao
import com.qurio.trivia.data.database.dao.UserProgressDao
import com.qurio.trivia.data.database.entity.CharacterEntity
import com.qurio.trivia.domain.model.Character
import com.qurio.trivia.domain.repository.CharacterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val characterDao: CharacterDao,
    private val userProgressDao: UserProgressDao
) : CharacterRepository {

    override suspend fun getSelectedCharacter(): Character = withContext(Dispatchers.IO) {
        val selectedName = userProgressDao.getUserProgress()?.selectedCharacter
        Character.fromName(selectedName ?: "") ?: Character.default()
    }

    override suspend fun selectCharacter(character: Character) = withContext(Dispatchers.IO) {
        userProgressDao.updateSelectedCharacter(character.characterName)
    }

    override suspend fun getAllCharactersWithUnlockStatus(): List<CharacterRepository.CharacterWithStatus> =
        withContext(Dispatchers.IO) {
            val selectedCharacter = getSelectedCharacter()

            // Get all unlocked characters from database
            val unlockedCharacters = characterDao.getAllCharacters()
                .filter { it.isUnlocked }
                .map { it.name }
                .toSet()

            // Combine enum data with unlock status
            Character.all().map { character ->
                CharacterRepository.CharacterWithStatus(
                    character = character,
                    isUnlocked = character.isFree() || character.characterName in unlockedCharacters,
                    isSelected = character == selectedCharacter
                )
            }
        }

    override suspend fun getUserCoins(): Int = withContext(Dispatchers.IO) {
        userProgressDao.getUserProgress()?.totalCoins ?: 0
    }

    override suspend fun isCharacterUnlocked(character: Character): Boolean =
        withContext(Dispatchers.IO) {
            // Free characters are always unlocked
            if (character.isFree()) return@withContext true

            // Check database for unlock status
            characterDao.getCharacterByName(character.characterName)?.isUnlocked ?: false
        }

    override suspend fun purchaseCharacter(
        character: Character
    ): CharacterRepository.PurchaseResult = withContext(Dispatchers.IO) {
        try {
            // Check if already unlocked
            if (isCharacterUnlocked(character)) {
                return@withContext CharacterRepository.PurchaseResult.Error("Character already unlocked")
            }

            // Get user progress
            val userProgress = userProgressDao.getUserProgress()
                ?: return@withContext CharacterRepository.PurchaseResult.Error("User not found")

            val currentCoins = userProgress.totalCoins
            val cost = character.unlockCost

            // Check if user has enough coins
            if (currentCoins < cost) {
                return@withContext CharacterRepository.PurchaseResult.InsufficientCoins(
                    currentCoins = currentCoins,
                    requiredCoins = cost
                )
            }

            // Deduct coins
            val newCoins = currentCoins - cost
            userProgressDao.updateCoins(newCoins)

            // Unlock character (insert or update)
            characterDao.insertCharacter(
                CharacterEntity(
                    name = character.characterName,
                    isUnlocked = true,
                    unlockedAt = System.currentTimeMillis()
                )
            )

            CharacterRepository.PurchaseResult.Success(remainingCoins = newCoins)
        } catch (e: Exception) {
            CharacterRepository.PurchaseResult.Error(e.message ?: "Unknown error")
        }
    }
}