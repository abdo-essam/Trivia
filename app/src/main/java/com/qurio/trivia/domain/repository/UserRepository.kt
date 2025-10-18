package com.qurio.trivia.domain.repository

import com.qurio.trivia.domain.model.PurchaseResult
import com.qurio.trivia.domain.model.UserProgress

interface UserRepository {
    suspend fun getUserProgress(): UserProgress?
    suspend fun getUserCoins(): Int
    suspend fun updateLives(lives: Int)
    suspend fun deductLife(): Int
    suspend fun updateCoins(coins: Int)
    suspend fun checkAndUpdateStreak(): UserProgress?
    suspend fun purchaseLife(cost: Int): PurchaseResult
}