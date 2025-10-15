package com.qurio.trivia.data.repository

import com.qurio.trivia.data.database.dao.GameResultDao
import com.qurio.trivia.data.database.dao.UserProgressDao
import com.qurio.trivia.data.mapper.GameResultMapper
import com.qurio.trivia.data.mapper.UserProgressMapper
import com.qurio.trivia.domain.model.Category
import com.qurio.trivia.domain.model.GameResult
import com.qurio.trivia.domain.model.UserProgress
import com.qurio.trivia.domain.repository.HomeRepository
import com.qurio.trivia.utils.StreakHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val userProgressDao: UserProgressDao,
    private val gameResultDao: GameResultDao,
    private val userProgressMapper: UserProgressMapper,
    private val gameResultMapper: GameResultMapper
) : HomeRepository {

    override suspend fun getUserProgress(): UserProgress? = withContext(Dispatchers.IO) {
        val entity = userProgressDao.getUserProgress()
        entity?.let { userProgressMapper.toDomain(it) }
    }

    override suspend fun getCategories(): List<Category> = withContext(Dispatchers.IO) {
        Category.all()
    }

    override suspend fun getLastGames(limit: Int): List<GameResult> = withContext(Dispatchers.IO) {
        val entities = gameResultDao.getLastGames(limit)
        gameResultMapper.toDomainList(entities)
    }

    override suspend fun updateLives(lives: Int) = withContext(Dispatchers.IO) {
        userProgressDao.updateLives(lives)
    }
    override suspend fun checkAndUpdateStreak(): UserProgress? = withContext(Dispatchers.IO) {
        val entity = userProgressDao.getUserProgress() ?: return@withContext null

        // Check if streak should be reset
        if (StreakHelper.shouldResetStreak(entity.lastPlayedDate)) {
            userProgressDao.updateStreak(
                streak = 0,
                date = "",
                days = ""
            )
        }

        getUserProgress()
    }
}