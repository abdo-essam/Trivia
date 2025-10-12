package com.qurio.trivia.domain.model

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val howToGet: String,
    val iconRes: Int,
    val iconLockedRes: Int,
    val isUnlocked: Boolean,
    val progress: Int = 0,
    val maxProgress: Int = 1
) {
    val progressPercentage: Int
        get() = if (maxProgress > 0) {
            ((progress.toFloat() / maxProgress) * 100).toInt()
        } else 0

    val isComplete: Boolean
        get() = progress >= maxProgress

    companion object {
        // Achievement IDs
        const val QUIZ_ROOKIE = "quiz_rookie"
        const val QUIZ_MASTER = "quiz_master"
        const val PERFECT_SCORE = "perfect_score"
        const val COIN_COLLECTOR = "coin_collector"
        const val STREAK_MASTER = "streak_master"
        const val CATEGORY_EXPLORER = "category_explorer"
        const val SPEED_DEMON = "speed_demon"
        const val KNOWLEDGE_HOARDER = "knowledge_hoarder"
    }
}