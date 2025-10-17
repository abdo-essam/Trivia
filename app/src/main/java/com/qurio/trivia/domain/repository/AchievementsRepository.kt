package com.qurio.trivia.domain.repository

import com.qurio.trivia.domain.model.UserAchievement

interface AchievementsRepository {
    suspend fun getAchievements(): List<UserAchievement>
    suspend fun getUnlockedCount(): Int
    suspend fun isAchievementUnlocked(achievementId: String): Boolean
    suspend fun checkAndUnlockAchievements(): List<UserAchievement>
    suspend fun initializeAchievements()
}