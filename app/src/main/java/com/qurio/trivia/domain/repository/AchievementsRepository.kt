package com.qurio.trivia.domain.repository

import com.qurio.trivia.domain.model.UserAchievement

interface AchievementsRepository {
    /**
     * Get all achievements with their current state
     */
    suspend fun getAchievements(): List<UserAchievement>

    /**
     * Get unlocked achievements count
     */
    suspend fun getUnlockedCount(): Int

    /**
     * Check if specific achievement is unlocked
     */
    suspend fun isAchievementUnlocked(achievementId: String): Boolean

    /**
     * Check and unlock achievements based on current user progress
     * Only calculates progress for locked achievements
     * Returns newly unlocked achievements
     */
    suspend fun checkAndUnlockAchievements(): List<UserAchievement>

    /**
     * Initialize achievements for new user
     */
    suspend fun initializeAchievements()
}