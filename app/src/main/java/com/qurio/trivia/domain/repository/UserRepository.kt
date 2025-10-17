package com.qurio.trivia.domain.repository

import com.qurio.trivia.domain.model.UserProgress

interface UserRepository {
    suspend fun getUserProgress(): UserProgress?
    suspend fun updateLives(lives: Int)
    suspend fun deductLife(): Int
    suspend fun updateCoins(coins: Int)
    suspend fun updateSelectedCharacter(character: String)
    suspend fun updateStreak(streak: Int, date: String, days: String)
    suspend fun checkAndUpdateStreak(): UserProgress?
}