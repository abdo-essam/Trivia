package com.qurio.trivia.domain.repository

import com.qurio.trivia.domain.model.Category
import com.qurio.trivia.domain.model.UserProgress

/**
 * Repository interface for games/categories operations
 */
interface GamesRepository {
    /**
     * Get all available game categories
     */
    suspend fun getAllCategories(): List<Category>

    /**
     * Get current user progress
     */
    suspend fun getUserProgress(): UserProgress?

    /**
     * Deduct one life and return updated count
     */
    suspend fun deductLife(): Int

    /**
     * Update user lives count
     */
    suspend fun updateLives(lives: Int)
}