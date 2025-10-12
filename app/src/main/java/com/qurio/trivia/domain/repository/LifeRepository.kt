package com.qurio.trivia.domain.repository

import com.qurio.trivia.domain.model.PurchaseResult

interface LifeRepository {
    suspend fun getUserCoins(): Int
    suspend fun getUserLives(): Int
    suspend fun purchaseLife(cost: Int): PurchaseResult
}