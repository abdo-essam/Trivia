package com.qurio.trivia.data.repository

import com.qurio.trivia.data.database.UserProgressDao
import com.qurio.trivia.data.model.Category
import com.qurio.trivia.data.model.UserProgress
import com.qurio.trivia.data.provider.DataProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GamesRepository @Inject constructor(
    private val userProgressDao: UserProgressDao
) {

    /**
     * Get all available game categories
     */
    suspend fun getAllCategories(): List<Category> {
        return DataProvider.getCategories()
    }

    /**
     * Get current user progress
     */
    suspend fun getUserProgress(): UserProgress? {
        return userProgressDao.getUserProgress()
    }

    /**
     * Update user lives
     */
    suspend fun updateLives(lives: Int) {
        userProgressDao.updateLives(lives)
    }

    /**
     * Deduct one life and return updated count
     */
    suspend fun deductLife(): Int {
        val userProgress = userProgressDao.getUserProgress()
        return if (userProgress != null && userProgress.lives > 0) {
            val newLives = userProgress.lives - 1
            userProgressDao.updateLives(newLives)
            newLives
        } else {
            0
        }
    }
}