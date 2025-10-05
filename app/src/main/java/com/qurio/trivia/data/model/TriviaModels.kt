package com.qurio.trivia.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class TriviaResponse(
    @SerializedName("response_code") val responseCode: Int,
    @SerializedName("results") val results: List<TriviaQuestion>
)

data class TriviaQuestion(
    val category: String,
    val type: String,
    val difficulty: String,
    val question: String,
    @SerializedName("correct_answer") val correctAnswer: String,
    @SerializedName("incorrect_answers") val incorrectAnswers: List<String>
) {
    fun getAllAnswers(): List<String> {
        return (incorrectAnswers + correctAnswer).shuffled()
    }
}

@Entity(tableName = "game_results")
data class GameResult(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val category: String,
    val difficulty: String,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val incorrectAnswers: Int,
    val skippedAnswers: Int,
    val stars: Int,
    val coins: Int,
    val timeTaken: Long,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey val id: Int = 1,
    val lives: Int = 4,
    val totalCoins: Int = 0,
    val awards: Int = 0,
    val selectedCharacter: String = "rika",
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val currentStreak: Int = 0,
    val lastPlayedDate: String = "", // Format: "yyyy-MM-dd"
    val streakDays: String = "" // Comma-separated days: "0,1,2" for S,M,T
)

data class Character(
    val name: String,
    val displayName: String,
    val age: String,
    val description: String,
    val imageRes: Int,
    val lockedImageRes: Int
)

data class Category(
    val id: Int,
    val name: String,
    val displayName: String,
    val imageRes: Int
)

enum class Difficulty(val value: String, val displayName: String) {
    EASY("easy", "Easy"),
    MEDIUM("medium", "Medium"),
    HARD("hard", "Hard")
}

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val iconRes: Int,
    val isUnlocked: Boolean,
    val progress: Int,
    val maxProgress: Int
)