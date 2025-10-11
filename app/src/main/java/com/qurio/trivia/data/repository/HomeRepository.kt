package com.qurio.trivia.data.repository

import com.qurio.trivia.data.database.GameResultDao
import com.qurio.trivia.data.database.UserProgressDao
import com.qurio.trivia.data.model.Category
import com.qurio.trivia.data.model.GameResult
import com.qurio.trivia.data.model.UserProgress
import com.qurio.trivia.data.provider.DataProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val userProgressDao: UserProgressDao,
    private val gameResultDao: GameResultDao
) {

    suspend fun getUserProgress(): UserProgress? {
        return userProgressDao.getUserProgress()
    }

    suspend fun getCategories(): List<Category> {
        return DataProvider.getCategories()
    }

    suspend fun getLastGames(limit: Int): List<GameResult> {
        return gameResultDao.getLastGames(limit)
    }

    suspend fun updateLives(lives: Int) {
        userProgressDao.updateLives(lives)
    }

    suspend fun updateCoins(coins: Int) {
        userProgressDao.updateCoins(coins)
    }

    suspend fun purchaseLife(currentCoins: Int, currentLives: Int, lifeCost: Int, maxLives: Int): PurchaseResult {
        return when {
            currentCoins < lifeCost -> PurchaseResult.NotEnoughCoins
            currentLives >= maxLives -> PurchaseResult.MaxLivesReached
            else -> {
                userProgressDao.updateCoins(currentCoins - lifeCost)
                userProgressDao.updateLives(currentLives + 1)
                PurchaseResult.Success
            }
        }
    }

    sealed class PurchaseResult {
        object Success : PurchaseResult()
        object NotEnoughCoins : PurchaseResult()
        object MaxLivesReached : PurchaseResult()
    }
}