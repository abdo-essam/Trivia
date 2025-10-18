package com.qurio.trivia.data.repository

import com.qurio.trivia.data.database.dao.GameResultDao
import com.qurio.trivia.data.mapper.GameResultMapper
import com.qurio.trivia.domain.model.GameResult
import com.qurio.trivia.domain.repository.GameResultRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameResultRepositoryImpl @Inject constructor(
    private val gameResultDao: GameResultDao,
    private val gameResultMapper: GameResultMapper
) : GameResultRepository {

    override suspend fun saveGameResult(gameResult: GameResult) = withContext(Dispatchers.IO) {
        val entity = gameResultMapper.toEntity(gameResult)
        gameResultDao.insertGameResult(entity)
    }

    override suspend fun getLastGames(limit: Int): List<GameResult> = withContext(Dispatchers.IO) {
        val entities = gameResultDao.getLastGames(limit)
        gameResultMapper.toDomainList(entities)
    }

    override suspend fun getAllGames(): List<GameResult> = withContext(Dispatchers.IO) {
        val entities = gameResultDao.getAllGames()
        gameResultMapper.toDomainList(entities)
    }
}