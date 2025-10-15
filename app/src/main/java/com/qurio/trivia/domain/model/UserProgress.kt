package com.qurio.trivia.domain.model

data class UserProgress(
    val lives: Int,
    val totalCoins: Int,
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

    fun getActiveDays(): Set<Int> {
        return streakDays
            .split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .toSet()
    }

    fun hasPlayedToday(): Boolean {
        return lastPlayedDate == com.qurio.trivia.utils.StreakHelper.getCurrentDate()
    }

    fun shouldResetStreak(): Boolean {
        return com.qurio.trivia.utils.StreakHelper.shouldResetStreak(lastPlayedDate)
    }
}