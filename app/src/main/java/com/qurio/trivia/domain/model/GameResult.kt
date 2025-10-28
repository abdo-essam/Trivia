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
)