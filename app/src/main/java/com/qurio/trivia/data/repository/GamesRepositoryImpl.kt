package com.qurio.trivia.data.repository

import com.qurio.trivia.data.database.dao.UserProgressDao
import com.qurio.trivia.data.mapper.UserProgressMapper
import com.qurio.trivia.domain.model.Category
import com.qurio.trivia.domain.model.UserProgress
import com.qurio.trivia.domain.repository.GamesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GamesRepositoryImpl @Inject constructor(
    private val userProgressDao: UserProgressDao,
    private val userProgressMapper: UserProgressMapper
) : GamesRepository {

    override suspend fun getAllCategories(): List<Category> = withContext(Dispatchers.IO) {
        Category.all()
    }

    override suspend fun getUserProgress(): UserProgress? = withContext(Dispatchers.IO) {
        val entity = userProgressDao.getUserProgress()
        entity?.let { userProgressMapper.toDomain(it) }
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

    override suspend fun updateLives(lives: Int) = withContext(Dispatchers.IO) {
        userProgressDao.updateLives(lives)
    }
}