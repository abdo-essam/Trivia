package com.qurio.trivia.domain.repository

import com.qurio.trivia.domain.model.GameResult
import com.qurio.trivia.domain.model.UserProgress

/**
 * Repository interface for game results
 */
interface GameResultRepository {

    /**
     * Save a game result
     */
    suspend fun saveGameResult(gameResult: GameResult): UserProgress?

    /**
     * Get all game results
     */
    suspend fun getAllGameResults(): List<GameResult>

    /**
     * Get last N game results
     */
    suspend fun getLastGameResults(limit: Int): List<GameResult>

    /**
     * Update user coins
     */
    suspend fun updateUserCoins(coinsToAdd: Int)

    /**
     * Get user progress
     */
    suspend fun getUserProgress(): UserProgress?

    /**
     * Update streak after a game
     */
    suspend fun updateStreakAfterGame()
}