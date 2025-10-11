package com.qurio.trivia.data.repository

import com.qurio.trivia.data.database.UserProgressDao
import com.qurio.trivia.domain.model.PurchaseResult
import com.qurio.trivia.domain.repository.LifeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LifeRepositoryImpl @Inject constructor(
    private val userProgressDao: UserProgressDao
) : LifeRepository {

    override suspend fun getUserCoins(): Int = withContext(Dispatchers.IO) {
        userProgressDao.getUserProgress()?.totalCoins ?: 0
    }

    override suspend fun getUserLives(): Int = withContext(Dispatchers.IO) {
        userProgressDao.getUserProgress()?.lives ?: 0
    }

    override suspend fun purchaseLife(cost: Int): PurchaseResult = withContext(Dispatchers.IO) {
        try {
            val userProgress = userProgressDao.getUserProgress()
                ?: return@withContext PurchaseResult.Error("User not found")

            val currentCoins = userProgress.totalCoins
            val currentLives = userProgress.lives

            // Validate sufficient funds
            if (currentCoins < cost) {
                return@withContext PurchaseResult.InsufficientCoins(currentCoins, cost)
            }

            // Process purchase
            val newCoins = currentCoins - cost
            val newLives = currentLives + 1

            userProgressDao.updateCoins(newCoins)
            userProgressDao.updateLives(newLives)

            PurchaseResult.Success(newCoins, newLives)
        } catch (e: Exception) {
            PurchaseResult.Error(e.message ?: "Purchase failed")
        }
    }
}