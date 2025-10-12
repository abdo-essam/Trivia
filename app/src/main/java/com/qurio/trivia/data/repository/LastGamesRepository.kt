package com.qurio.trivia.data.repository

import com.qurio.trivia.data.database.dao.GameResultDao
import com.qurio.trivia.domain.model.GameResult
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
        // todo: implement real data fetching
       // return gameResultDao.getAllGames()
        return GameResult.sampleData()
    }

    /**
     * Get limited number of recent games
     */
    suspend fun getRecentGames(limit: Int): List<GameResult> {
        //return gameResultDao.getLastGames(limit)
        return GameResult.sampleData().take(limit)
    }
}