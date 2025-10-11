package com.qurio.trivia.domain.model


data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val howToGet: String,
    val iconRes: Int,
    val isUnlocked: Boolean,
    val unlockedDate: Long? = null
) {
    companion object {
        // Achievement IDs
        const val FIRST_GAME = "first_game"
        const val EXPLORER = "explorer"
        const val COLLECTOR = "collector"
        const val STREAK_MASTER = "streak_master"
        const val PERFECT_SCORE = "perfect_score"
        const val SPEED_DEMON = "speed_demon"
        const val CATEGORY_MASTER = "category_master"
        const val COIN_COLLECTOR = "coin_collector"
    }
}