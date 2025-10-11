package com.qurio.trivia.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.qurio.trivia.data.model.UserProgress

@Dao
interface UserProgressDao {
    @Query("SELECT * FROM user_progress WHERE id = 1")
    suspend fun getUserProgress(): UserProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserProgress(userProgress: UserProgress)

    @Query("UPDATE user_progress SET lives = :lives WHERE id = 1")
    suspend fun updateLives(lives: Int)

    @Query("UPDATE user_progress SET totalCoins = :coins WHERE id = 1")
    suspend fun updateCoins(coins: Int)

    @Query("UPDATE user_progress SET selectedCharacter = :character WHERE id = 1")
    suspend fun updateSelectedCharacter(character: String)

    @Query("UPDATE user_progress SET soundEnabled = :enabled WHERE id = 1")
    suspend fun updateSoundEnabled(enabled: Boolean)

    @Query("UPDATE user_progress SET musicEnabled = :enabled WHERE id = 1")
    suspend fun updateMusicEnabled(enabled: Boolean)

    @Query("UPDATE user_progress SET currentStreak = :streak, lastPlayedDate = :date, streakDays = :days WHERE id = 1")
    suspend fun updateStreak(streak: Int, date: String, days: String)

    @Query("SELECT currentStreak FROM user_progress WHERE id = 1")
    suspend fun getCurrentStreak(): Int?

    @Query("UPDATE user_progress SET awards = :awards WHERE id = 1")
    suspend fun updateAwards(awards: Int)


}