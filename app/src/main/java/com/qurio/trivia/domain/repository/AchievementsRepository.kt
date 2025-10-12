package com.qurio.trivia.domain.repository

import com.qurio.trivia.domain.model.Achievement

/**
 * Repository interface for achievements
 */
interface AchievementsRepository {

    /**
     * Get all achievements with unlock status and progress
     */
    suspend fun getAchievements(): List<Achievement>

    /**
     * Get unlocked achievements count
     */
    suspend fun getUnlockedCount(): Int

    /**
     * Check if specific achievement is unlocked
     */
    suspend fun isAchievementUnlocked(achievementId: String): Boolean
}