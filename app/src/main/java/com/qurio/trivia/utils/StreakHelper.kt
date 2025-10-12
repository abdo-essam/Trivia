package com.qurio.trivia.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility for managing streak logic
 */
object StreakHelper {
    private const val DATE_FORMAT = "yyyy-MM-dd"

    /**
     * Get current date as string
     */
    fun getCurrentDate(): String {
        return SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date())
    }

    /**
     * Get day of week (0 = Sunday, 6 = Saturday)
     */
    fun getDayOfWeek(dateString: String = getCurrentDate()): Int {
        val calendar = Calendar.getInstance()
        if (dateString.isNotEmpty()) {
            val date = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).parse(dateString)
            date?.let { calendar.time = it }
        }
        return calendar.get(Calendar.DAY_OF_WEEK) - 1 // 0-based (Sunday = 0)
    }

    /**
     * Check if dates are consecutive days
     */
    fun areConsecutiveDays(lastDate: String, currentDate: String): Boolean {
        if (lastDate.isEmpty()) return false

        val format = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        val last = format.parse(lastDate) ?: return false
        val current = format.parse(currentDate) ?: return false

        val diffInMillis = current.time - last.time
        val diffInDays = diffInMillis / (1000 * 60 * 60 * 24)

        return diffInDays == 1L
    }

    /**
     * Check if date is today
     */
    fun isToday(dateString: String): Boolean {
        return dateString == getCurrentDate()
    }

    /**
     * Check if streak should reset (more than 1 day gap)
     */
    fun shouldResetStreak(lastDate: String, currentDate: String = getCurrentDate()): Boolean {
        if (lastDate.isEmpty()) return false
        if (isToday(lastDate)) return false

        val format = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        val last = format.parse(lastDate) ?: return true
        val current = format.parse(currentDate) ?: return true

        val diffInMillis = current.time - last.time
        val diffInDays = diffInMillis / (1000 * 60 * 60 * 24)

        return diffInDays > 1
    }

    /**
     * Parse active days from string
     */
    fun parseActiveDays(streakDays: String): Set<Int> {
        return streakDays
            .split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .toSet()
    }

    /**
     * Add day to active days
     */
    fun addActiveDay(currentDays: String, dayIndex: Int): String {
        val days = parseActiveDays(currentDays).toMutableSet()
        days.add(dayIndex)
        return days.sorted().joinToString(",")
    }

    /**
     * Calculate new streak data after playing
     */
    fun calculateNewStreak(
        lastPlayedDate: String,
        currentStreak: Int,
        streakDays: String
    ): StreakData {
        val today = getCurrentDate()

        return when {
            // First time playing
            lastPlayedDate.isEmpty() -> {
                StreakData(
                    streak = 1,
                    date = today,
                    days = getDayOfWeek().toString()
                )
            }

            // Already played today
            isToday(lastPlayedDate) -> {
                StreakData(
                    streak = currentStreak,
                    date = today,
                    days = streakDays
                )
            }

            // Consecutive day
            areConsecutiveDays(lastPlayedDate, today) -> {
                StreakData(
                    streak = currentStreak + 1,
                    date = today,
                    days = addActiveDay(streakDays, getDayOfWeek())
                )
            }

            // Streak broken
            else -> {
                StreakData(
                    streak = 1,
                    date = today,
                    days = getDayOfWeek().toString()
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