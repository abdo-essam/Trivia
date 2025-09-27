package com.qurio.trivia.data.database

import androidx.room.*
import com.qurio.trivia.data.model.GameResult

@Dao
interface GameResultDao {
    @Query("SELECT * FROM game_results ORDER BY date DESC")
    suspend fun getAllGameResults(): List<GameResult>

    @Insert
    suspend fun insertGameResult(gameResult: GameResult)

    @Query("DELETE FROM game_results")
    suspend fun clearGameResults()
}