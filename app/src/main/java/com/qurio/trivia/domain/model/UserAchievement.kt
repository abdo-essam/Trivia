package com.qurio.trivia.domain.model

import java.io.Serializable

data class UserAchievement(
    val achievement: Achievement,
    val isUnlocked: Boolean,
    val unlockedAt: Long? = null,
    val currentProgress: Int = 0
) : Serializable {
    val id: String get() = achievement.id
    val title: String get() = achievement.title
    val description: String get() = achievement.description
    val howToGet: String get() = achievement.howToGet
    val maxProgress: Int get() = achievement.maxProgress
}