package com.qurio.trivia.domain.repository

import com.qurio.trivia.domain.model.Category
import com.qurio.trivia.domain.model.GameResult
import com.qurio.trivia.domain.model.UserProgress

interface HomeRepository {
    suspend fun getUserProgress(): UserProgress?
    suspend fun getCategories(): List<Category>
    suspend fun getLastGames(limit: Int): List<GameResult>
    suspend fun updateLives(lives: Int)
    /**
     * Check and update streak status
     */
    suspend fun checkAndUpdateStreak(): UserProgress?
}