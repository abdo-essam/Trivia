package com.qurio.trivia.domain.repository

import com.qurio.trivia.domain.model.GameResult

interface GameResultRepository {
    suspend fun saveGameResult(gameResult: GameResult)
    suspend fun getLastGames(limit: Int): List<GameResult>
    suspend fun getAllGames(): List<GameResult>
    suspend fun getGameCount(): Int
}