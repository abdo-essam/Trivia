package com.qurio.trivia.domain.repository

import com.qurio.trivia.domain.model.PurchaseResult
import com.qurio.trivia.domain.model.UserProgress

interface UserRepository {
    suspend fun getUserProgress(): UserProgress?
    suspend fun getUserCoins(): Int
    suspend fun getUserLives(): Int
    suspend fun updateLives(lives: Int)
    suspend fun deductLife(): Int
    suspend fun updateCoins(coins: Int)
    suspend fun updateSelectedCharacter(character: String)
    suspend fun updateStreak(streak: Int, date: String, days: String)
    suspend fun checkAndUpdateStreak(): UserProgress?
    suspend fun purchaseLife(cost: Int): PurchaseResult

}