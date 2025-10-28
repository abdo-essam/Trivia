package com.qurio.trivia.domain.model

data class UserProgress(
    val lives: Int,
    val totalCoins: Int,
    val selectedCharacter: String,
    val currentStreak: Int,
    val lastPlayedDate: String,
    val streakDays: String
) {
    fun hasEnoughLives(): Boolean = lives > 0
}