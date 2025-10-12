package com.qurio.trivia.domain.model

data class GameResult(
    val id: Long,
    val date: String,
    val category: String, // Display name stored
    val totalQuestions: Int,
    val correctAnswers: Int,
    val incorrectAnswers: Int,
    val skippedAnswers: Int,
    val stars: Int,
    val coins: Int,
    val timeTaken: Long,
    val timestamp: Long
) {
    companion object {
        val DEFAULT = GameResult(
            id = 0L,
            date = "",
            category = "",
            totalQuestions = 0,
            correctAnswers = 0,
            incorrectAnswers = 0,
            skippedAnswers = 0,
            stars = 0,
            coins = 0,
            timeTaken = 0L,
            timestamp = 0L
        )
    }

    /**
     * Calculate accuracy percentage
     */
    val accuracyPercentage: Int
        get() = if (totalQuestions > 0) {
            ((correctAnswers.toFloat() / totalQuestions) * 100).toInt()
        } else 0

    /**
     * Format time taken as readable string
     */
    val formattedTime: String
        get() {
            val minutes = (timeTaken / 1000) / 60
            val seconds = (timeTaken / 1000) % 60
            return String.format("%dm %02ds", minutes, seconds)
        }

    /**
     * Get category enum from stored name (if it matches)
     */
    fun getCategoryEnum(): Category? {
        return Category.entries.find {
            it.displayName.equals(category, ignoreCase = true)
        }
    }

    /**
     * Get category image resource
     */
    val categoryImageRes: Int?
        get() = getCategoryEnum()?.imageRes

    /**
     * Check if game was perfect (all answers correct)
     */
    val isPerfect: Boolean
        get() = correctAnswers == totalQuestions && incorrectAnswers == 0 && skippedAnswers == 0

    /**
     * Check if game was won (at least 1 star)
     */
    val isWon: Boolean
        get() = stars > 0
}