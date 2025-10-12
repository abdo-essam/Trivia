package com.qurio.trivia.domain.model

data class GameResult(
    val id: Long,
    val date: String,
    val category: String,
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

        fun sampleData(): List<GameResult> {
            return listOf(
                GameResult(
                    id = 1L,
                    date = "2024-06-01",
                    category = "Science",
                    totalQuestions = 10,
                    correctAnswers = 8,
                    incorrectAnswers = 1,
                    skippedAnswers = 1,
                    stars = 4,
                    coins = 50,
                    timeTaken = 120000L,
                    timestamp = 1712131200000L
                ),
                GameResult(
                    id = 2L,
                    date = "2024-06-02",
                    category = "History",
                    totalQuestions = 10,
                    correctAnswers = 6,
                    incorrectAnswers = 3,
                    skippedAnswers = 1,
                    stars = 3,
                    coins = 30,
                    timeTaken = 150000L,
                    timestamp = 1712217600000L
                ),
                GameResult(
                    id = 3L,
                    date = "2024-06-03",
                    category = "Geography",
                    totalQuestions = 10,
                    correctAnswers = 9,
                    incorrectAnswers = 0,
                    skippedAnswers = 1,
                    stars = 5,
                    coins = 70,
                    timeTaken = 110000L,
                    timestamp = 1712304000000L
                )
            )
        }
    }
    val accuracyPercentage: Int
        get() = if (totalQuestions > 0) {
            ((correctAnswers.toFloat() / totalQuestions) * 100).toInt()
        } else 0

    val formattedTime: String
        get() {
            val minutes = (timeTaken / 1000) / 60
            val seconds = (timeTaken / 1000) % 60
            return String.format("%dm %02ds", minutes, seconds)
        }
}