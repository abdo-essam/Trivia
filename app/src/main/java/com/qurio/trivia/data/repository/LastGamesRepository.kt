package com.qurio.trivia.data.repository

import com.qurio.trivia.data.database.GameResultDao
import com.qurio.trivia.data.model.GameResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LastGamesRepository @Inject constructor(
    private val gameResultDao: GameResultDao
) {

    /**
     * Get all game results ordered by most recent
     */
    suspend fun getAllGames(): List<GameResult> {
        return gameResultDao.getAllGames()
    }

    /**
     * Get limited number of recent games
     */
    suspend fun getRecentGames(limit: Int): List<GameResult> {
        return gameResultDao.getLastGames(limit)
    }
}