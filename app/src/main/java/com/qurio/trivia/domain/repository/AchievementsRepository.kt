package com.qurio.trivia.domain.repository

import com.qurio.trivia.domain.model.AchievementState

/**
 * Repository interface for achievements
 */
interface AchievementsRepository {
    /**
     * Get all achievements with their current state
     */
    suspend fun getAchievements(): List<AchievementState>

    /**
     * Get unlocked achievements count
     */
    suspend fun getUnlockedCount(): Int

    /**
     * Check if specific achievement is unlocked
     */
    suspend fun isAchievementUnlocked(achievementId: String): Boolean
}