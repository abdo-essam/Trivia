package com.qurio.trivia.data.repository

import android.util.Log
import com.qurio.trivia.data.database.dao.GameResultDao
import com.qurio.trivia.data.database.dao.UserProgressDao
import com.qurio.trivia.data.mapper.GameResultMapper
import com.qurio.trivia.data.mapper.UserProgressMapper
import com.qurio.trivia.domain.model.GameResult
import com.qurio.trivia.domain.model.UserProgress
import com.qurio.trivia.domain.repository.GameResultRepository
import com.qurio.trivia.utils.StreakHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of GameResultRepository
 * Handles game result persistence and user progress updates
 */
@Singleton
class GameResultRepositoryImpl @Inject constructor(
    private val gameResultDao: GameResultDao,
    private val userProgressDao: UserProgressDao,
    private val gameResultMapper: GameResultMapper,
    private val userProgressMapper: UserProgressMapper
) : GameResultRepository {

    companion object {
        private const val TAG = "GameResultRepositoryImpl"
    }

    override suspend fun saveGameResult(gameResult: GameResult): UserProgress? =
        withContext(Dispatchers.IO) {
            // Save game result
            val entity = gameResultMapper.toEntity(gameResult)
            gameResultDao.insertGameResult(entity)

            // Update streak
            updateStreakAfterGame()

            // Return updated user progress
            val updatedEntity = userProgressDao.getUserProgress()
            updatedEntity?.let { userProgressMapper.toDomain(it) }
        }

    override suspend fun getAllGameResults(): List<GameResult> = withContext(Dispatchers.IO) {
        try {
            val entities = gameResultDao.getAllGames()
            gameResultMapper.toDomainList(entities)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting all game results", e)
            emptyList()
        }
    }

    override suspend fun getLastGameResults(limit: Int): List<GameResult> = withContext(Dispatchers.IO) {
        try {
            val entities = gameResultDao.getLastGames(limit)
            gameResultMapper.toDomainList(entities)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting last game results", e)
            emptyList()
        }
    }

    override suspend fun updateUserCoins(coinsToAdd: Int)  {
        try {
            val userProgressEntity = userProgressDao.getUserProgress()
            userProgressEntity?.let {
                val newCoins = it.totalCoins + coinsToAdd
                userProgressDao.updateCoins(newCoins)
                Log.d(TAG, "✓ User coins updated: ${it.totalCoins} -> $newCoins")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user coins", e)
            throw e
        }
    }

    override suspend fun updateUserAwards(starsToAdd: Int) {
        try {
            val userProgressEntity = userProgressDao.getUserProgress()
            userProgressEntity?.let {
                val newAwards = it.awards + starsToAdd
                userProgressDao.updateAwards(newAwards)
                Log.d(TAG, "✓ User awards updated: ${it.awards} -> $newAwards")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user awards", e)
            throw e
        }
    }

    override suspend fun getUserProgress(): UserProgress? = withContext(Dispatchers.IO) {
        try {
            val entity = userProgressDao.getUserProgress()
            entity?.let { userProgressMapper.toDomain(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user progress", e)
            null
        }
    }

    override suspend fun updateStreakAfterGame() = withContext(Dispatchers.IO) {
        val userProgress = userProgressDao.getUserProgress() ?: return@withContext

        val newStreak = StreakHelper.calculateNewStreak(
            lastPlayedDate = userProgress.lastPlayedDate,
            currentStreak = userProgress.currentStreak,
            streakDays = userProgress.streakDays
        )

        userProgressDao.updateStreak(
            streak = newStreak.streak,
            date = newStreak.date,
            days = newStreak.days
        )
    }
}