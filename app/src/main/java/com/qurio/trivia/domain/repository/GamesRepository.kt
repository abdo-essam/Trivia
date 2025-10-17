package com.qurio.trivia.domain.repository

import com.qurio.trivia.domain.model.Category
import com.qurio.trivia.domain.model.UserProgress

interface GamesRepository {
    suspend fun getAllCategories(): List<Category>
    suspend fun getUserProgress(): UserProgress?
    suspend fun deductLife(): Int
    suspend fun updateLives(lives: Int)
}