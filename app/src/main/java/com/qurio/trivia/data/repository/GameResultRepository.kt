package com.qurio.trivia.data.repository

import com.qurio.trivia.data.database.GameResultDao
import com.qurio.trivia.data.database.UserProgressDao
import com.qurio.trivia.data.model.GameResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameResultRepository @Inject constructor(
    private val gameResultDao: GameResultDao,
    private val userProgressDao: UserProgressDao
) {

    /**
     * Save game result to database
     */
    suspend fun saveGameResult(gameResult: GameResult) {
        return gameResultDao.insertGameResult(gameResult)
    }

    /**
     * Update user coins after game
     */
    suspend fun updateUserCoins(coinsToAdd: Int) {
        val userProgress = userProgressDao.getUserProgress()
        userProgress?.let {
            val newCoins = it.totalCoins + coinsToAdd
            userProgressDao.updateCoins(newCoins)
        }
    }

    /**
     * Update user awards (stars)
     */
    suspend fun updateUserAwards(starsToAdd: Int) {
        val userProgress = userProgressDao.getUserProgress()
        userProgress?.let {
            val newAwards = it.awards + starsToAdd
            userProgressDao.updateAwards(newAwards)
        }
    }

    /**
     * Get user's current stats
     */
    suspend fun getUserProgress() = userProgressDao.getUserProgress()
}