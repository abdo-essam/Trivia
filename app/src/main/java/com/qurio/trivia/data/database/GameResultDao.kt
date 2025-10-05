package com.qurio.trivia.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.qurio.trivia.data.model.GameResult

@Dao
interface GameResultDao {

    @Insert
    suspend fun insertGameResult(gameResult: GameResult)

    @Query("SELECT * FROM game_results ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getLastGames(limit: Int): List<GameResult>

    @Query("SELECT * FROM game_results ORDER BY timestamp DESC")
    suspend fun getAllGames(): List<GameResult>

    @Query("SELECT COUNT(*) FROM game_results")
    suspend fun getGameCount(): Int

    @Query("DELETE FROM game_results")
    suspend fun deleteAllGames()
}