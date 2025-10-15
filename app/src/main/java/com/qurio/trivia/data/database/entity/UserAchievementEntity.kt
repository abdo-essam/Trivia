package com.qurio.trivia.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_achievements")
data class UserAchievementEntity(
    @PrimaryKey
    val achievementId: String,
    val isUnlocked: Boolean,
    val unlockedAt: Long? = null,
    val currentProgress: Int = 0
)