package com.qurio.trivia.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object FormatUtils {

    private const val DATE_INPUT_FORMAT = "yyyy-MM-dd"
    private const val DATE_OUTPUT_FORMAT = "dd-MM-yyyy"
    private const val DATE_STORAGE_FORMAT = "MMM dd, yyyy"

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

    fun getCurrentFormattedDate(): String {
        val dateFormat = SimpleDateFormat(DATE_STORAGE_FORMAT, Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun formatTime(timeInMillis: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillis) % 60

        return if (minutes > 0) {
            "${minutes}m ${seconds}sec"
        } else {
            "${seconds}sec"
        }
    }

    fun formatCoins(coins: Int): String {
        return coins.toString()
    }
}