package com.qurio.trivia.domain.repository

interface GameRepository {
    suspend fun updateLives(lives: Int)
    suspend fun updateCoins(coins: Int)
    suspend fun getUserLives(): Int?
    suspend fun getUserCoins(): Int?
}