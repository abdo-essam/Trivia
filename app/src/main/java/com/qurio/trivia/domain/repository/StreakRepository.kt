package com.qurio.trivia.domain.repository

/**
 * Repository for streak management
 */
interface StreakRepository {
    /**
     * Update streak after completing a game
     */
    suspend fun updateStreakAfterGame()

    /**
     * Get current streak info
     */
    suspend fun getCurrentStreakInfo(): StreakInfo

    /**
     * Reset streak
     */
    suspend fun resetStreak()

    data class StreakInfo(
        val currentStreak: Int,
        val lastPlayedDate: String,
        val activeDays: Set<Int>
    )
}