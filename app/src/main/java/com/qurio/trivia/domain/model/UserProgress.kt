package com.qurio.trivia.domain.model

data class UserProgress(
    val lives: Int,
    val totalCoins: Int,
    val awards: Int,
    val selectedCharacter: String,
    val soundEnabled: Boolean,
    val musicEnabled: Boolean,
    val currentStreak: Int,
    val lastPlayedDate: String,
    val streakDays: String
) {
    companion object {
        val DEFAULT = UserProgress(
            lives = 4,
            totalCoins = 0,
            awards = 0,
            selectedCharacter = "rika",
            soundEnabled = true,
            musicEnabled = true,
            currentStreak = 0,
            lastPlayedDate = "",
            streakDays = ""
        )
    }

    fun hasEnoughLives(): Boolean = lives > 0

    fun canAfford(cost: Int): Boolean = totalCoins >= cost

    /**
     * Get active days as set of integers (0-6)
     */
    fun getActiveDays(): Set<Int> {
        return streakDays
            .split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .toSet()
    }

    /**
     * Check if played today
     */
    fun hasPlayedToday(): Boolean {
        return lastPlayedDate == com.qurio.trivia.utils.StreakHelper.getCurrentDate()
    }

    /**
     * Check if streak should be reset
     */
    fun shouldResetStreak(): Boolean {
        return com.qurio.trivia.utils.StreakHelper.shouldResetStreak(lastPlayedDate)
    }
}