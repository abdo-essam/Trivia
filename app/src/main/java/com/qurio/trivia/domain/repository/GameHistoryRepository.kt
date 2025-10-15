package com.qurio.trivia.domain.repository

import com.qurio.trivia.domain.model.GameResult

/**
 * Repository interface for game history operations
 */
interface GameHistoryRepository {
    /**
     * Get all game results ordered by most recent
     */
    suspend fun getAllGames(): List<GameResult>

    /**
     * Get limited number of recent games
     */
    suspend fun getRecentGames(limit: Int): List<GameResult>

    /**
     * Get total number of games played
     */
    suspend fun getGameCount(): Int
}