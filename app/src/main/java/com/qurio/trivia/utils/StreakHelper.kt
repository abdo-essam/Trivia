package com.qurio.trivia.utils

/**
 * Utility for managing streak logic
 */
object StreakHelper {

    /**
     * Parse active days from string
     */
    fun parseActiveDays(streakDays: String?): Set<Int> {
        if (streakDays.isNullOrEmpty()) return emptySet()
        return streakDays
            .split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .filter { it in 0..6 } // Valid day range
            .toSet()
    }

    /**
     * Add day to active days
     */
    fun addActiveDay(currentDays: String?, dayIndex: Int): String {
        if (dayIndex !in 0..6) return currentDays ?: ""

        val days = parseActiveDays(currentDays).toMutableSet()
        days.add(dayIndex)
        return days.sorted().joinToString(",")
    }

    /**
     * Calculate new streak data when opening app
     */
    fun calculateNewStreak(
        lastPlayedDate: String?,
        currentStreak: Int,
        streakDays: String?
    ): StreakData {
        val today = DateUtils.getCurrentStreakDate()
        val todayDayOfWeek = DateUtils.getCurrentDayOfWeek()

        return when {
            // First time playing or empty date
            lastPlayedDate.isNullOrEmpty() -> {
                StreakData(
                    streak = 1,
                    date = today,
                    days = todayDayOfWeek.toString()
                )
            }

            // Already updated today - no change
            DateUtils.isToday(lastPlayedDate) -> {
                StreakData(
                    streak = currentStreak,
                    date = today,
                    days = streakDays ?: todayDayOfWeek.toString()
                )
            }

            // Consecutive day - increment streak
            DateUtils.areConsecutiveDays(lastPlayedDate, today) -> {
                StreakData(
                    streak = currentStreak + 1,
                    date = today,
                    days = addActiveDay(streakDays, todayDayOfWeek)
                )
            }

            // Streak broken - reset
            else -> {
                StreakData(
                    streak = 1,
                    date = today,
                    days = todayDayOfWeek.toString()
                )
            }
        }
    }

    data class StreakData(
        val streak: Int,
        val date: String,
        val days: String
    )
}