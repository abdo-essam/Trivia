package com.qurio.trivia.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateUtils {

    // Date formats
    private const val DATE_INPUT_FORMAT = "yyyy-MM-dd"
    private const val DATE_OUTPUT_FORMAT = "dd-MM-yyyy"
    private const val DATE_STORAGE_FORMAT = "MMM dd, yyyy"

    /**
     * Format date from input format to display format
     */
    fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat(DATE_INPUT_FORMAT, Locale.getDefault())
            val outputFormat = SimpleDateFormat(DATE_OUTPUT_FORMAT, Locale.getDefault())
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }

    /**
     * Get current date in storage format (MMM dd, yyyy)
     */
    fun getCurrentFormattedDate(): String {
        val dateFormat = SimpleDateFormat(DATE_STORAGE_FORMAT, Locale.getDefault())
        return dateFormat.format(Date())
    }

    /**
     * Get current date in streak format (yyyy-MM-dd)
     */
    fun getCurrentStreakDate(): String {
        val dateFormat = SimpleDateFormat(DATE_INPUT_FORMAT, Locale.getDefault())
        return dateFormat.format(Date())
    }

    /**
     * Format time in milliseconds to readable string
     */
    fun formatTime(timeInMillis: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillis) % 60

        return if (minutes > 0) {
            "${minutes}m ${seconds}sec"
        } else {
            "${seconds}sec"
        }
    }

    /**
     * Format coins to string
     */
    fun formatCoins(coins: Int): String {
        return coins.toString()
    }

    /**
     * Parse date string to Date object
     */
    fun parseDate(dateString: String): Date? {
        return try {
            SimpleDateFormat(DATE_INPUT_FORMAT, Locale.getDefault()).parse(dateString)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Get current day of week (0 = Sunday, 6 = Saturday)
     */
    fun getCurrentDayOfWeek(): Int {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1
    }

    /**
     * Check if date string is today
     */
    fun isToday(dateString: String?): Boolean {
        if (dateString.isNullOrEmpty()) return false
        return dateString == getCurrentStreakDate()
    }

    /**
     * Check if two dates are consecutive days
     */
    fun areConsecutiveDays(lastDate: String?, currentDate: String = getCurrentStreakDate()): Boolean {
        if (lastDate.isNullOrEmpty()) return false

        val last = parseDate(lastDate) ?: return false
        val current = parseDate(currentDate) ?: return false

        val diffInMillis = current.time - last.time
        val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)

        return diffInDays == 1L
    }

}