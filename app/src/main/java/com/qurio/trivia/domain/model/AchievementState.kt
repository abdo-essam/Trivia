package com.qurio.trivia.domain.model

/**
 * Represents the current state of an achievement for a user
 */
data class AchievementState(
    val achievement: Achievement,
    val progress: Int = 0,
    val isUnlocked: Boolean = false
) {
    val id: String get() = achievement.id
    val title: String get() = achievement.title
    val description: String get() = achievement.description
    val howToGet: String get() = achievement.howToGet
    val maxProgress: Int get() = achievement.maxProgress
}