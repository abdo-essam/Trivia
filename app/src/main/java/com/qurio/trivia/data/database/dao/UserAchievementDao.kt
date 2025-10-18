package com.qurio.trivia.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.qurio.trivia.data.database.entity.UserAchievementEntity

@Dao
interface UserAchievementDao {

    @Query("SELECT * FROM user_achievements")
    suspend fun getAllAchievements(): List<UserAchievementEntity>

    @Query("SELECT * FROM user_achievements WHERE isUnlocked = 0")
    suspend fun getLockedAchievements(): List<UserAchievementEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAchievements(achievements: List<UserAchievementEntity>)

    @Query("UPDATE user_achievements SET isUnlocked = 1, unlockedAt = :unlockedAt WHERE achievementId = :achievementId AND isUnlocked = 0")
    suspend fun unlockAchievement(achievementId: String, unlockedAt: Long): Int

    @Query("UPDATE user_achievements SET currentProgress = :progress WHERE achievementId = :achievementId AND isUnlocked = 0")
    suspend fun updateProgress(achievementId: String, progress: Int)

    @Query("SELECT COUNT(*) FROM user_achievements WHERE isUnlocked = 1")
    suspend fun getUnlockedCount(): Int

    @Query("SELECT isUnlocked FROM user_achievements WHERE achievementId = :achievementId")
    suspend fun isAchievementUnlocked(achievementId: String): Boolean?
}