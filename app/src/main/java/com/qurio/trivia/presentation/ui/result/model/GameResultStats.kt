package com.qurio.trivia.presentation.ui.result.model

data class GameResultStats(
    val correct: Int,
    val incorrect: Int,
    val skipped: Int,
    val stars: Int,
    val coins: Int,
    val isWon: Boolean,
    val percentage: Int
)