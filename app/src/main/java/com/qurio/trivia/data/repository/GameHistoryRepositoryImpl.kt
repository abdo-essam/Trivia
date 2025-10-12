package com.qurio.trivia.data.repository

import com.qurio.trivia.data.database.dao.GameResultDao
import com.qurio.trivia.data.mapper.GameResultMapper
import com.qurio.trivia.domain.model.GameResult
import com.qurio.trivia.domain.repository.GameHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameHistoryRepositoryImpl @Inject constructor(
    private val gameResultDao: GameResultDao,
    private val gameResultMapper: GameResultMapper
) : GameHistoryRepository {

    override suspend fun getAllGames(): List<GameResult> = withContext(Dispatchers.IO) {
        val entities = gameResultDao.getAllGames()
        gameResultMapper.toDomainList(entities)
    }

    override suspend fun getRecentGames(limit: Int): List<GameResult> = withContext(Dispatchers.IO) {
        val entities = gameResultDao.getLastGames(limit)
        gameResultMapper.toDomainList(entities)
    }

    override suspend fun getGameCount(): Int = withContext(Dispatchers.IO) {
        gameResultDao.getGameCount()
    }
}