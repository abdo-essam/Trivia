package com.qurio.trivia.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.qurio.trivia.data.database.entity.GameResultEntity

@Dao
interface GameResultDao {
    @Insert
    suspend fun insertGameResult(gameResult: GameResultEntity)

    @Query("SELECT * FROM game_results ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getLastGames(limit: Int): List<GameResultEntity>

    @Query("SELECT * FROM game_results ORDER BY timestamp DESC")
    suspend fun getAllGames(): List<GameResultEntity>

    @Query("SELECT COUNT(*) FROM game_results")
    suspend fun getGameCount(): Int

    @Query("DELETE FROM game_results")
    suspend fun deleteAllGames()
}