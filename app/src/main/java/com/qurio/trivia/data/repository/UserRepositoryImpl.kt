package com.qurio.trivia.data.repository

import com.qurio.trivia.data.database.dao.UserProgressDao
import com.qurio.trivia.data.mapper.UserProgressMapper
import com.qurio.trivia.domain.model.PurchaseResult
import com.qurio.trivia.domain.model.UserProgress
import com.qurio.trivia.domain.repository.UserRepository
import com.qurio.trivia.utils.StreakHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userProgressDao: UserProgressDao,
    private val userProgressMapper: UserProgressMapper
) : UserRepository {

    override suspend fun getUserProgress(): UserProgress? = withContext(Dispatchers.IO) {
        val entity = userProgressDao.getUserProgress()
        entity?.let { userProgressMapper.toDomain(it) }
    }

    override suspend fun getUserCoins(): Int = withContext(Dispatchers.IO) {
        userProgressDao.getUserProgress()?.totalCoins ?: 0
    }


    override suspend fun updateLives(lives: Int) = withContext(Dispatchers.IO) {
        userProgressDao.updateLives(lives)
    }

    override suspend fun deductLife(): Int = withContext(Dispatchers.IO) {
        val userProgress = userProgressDao.getUserProgress()
        if (userProgress != null && userProgress.lives > 0) {
            val newLives = userProgress.lives - 1
            userProgressDao.updateLives(newLives)
            newLives
        } else {
            0
        }
    }

    override suspend fun updateCoins(coins: Int) = withContext(Dispatchers.IO) {
        userProgressDao.updateCoins(coins)
    }

    override suspend fun checkAndUpdateStreak(): UserProgress? = withContext(Dispatchers.IO) {
        val entity = userProgressDao.getUserProgress() ?: return@withContext null

        if (StreakHelper.shouldResetStreak(entity.lastPlayedDate)) {
            userProgressDao.updateStreak(
                streak = 0,
                date = "",
                days = ""
            )
        }

        getUserProgress()
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