package com.qurio.trivia.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val id: Int = 1,
    val lives: Int = 4,
    val totalCoins: Int = 0,
    val selectedCharacter: String = "rika",
    val currentStreak: Int = 0,
    val lastPlayedDate: String = "",
    val streakDays: String = ""
)