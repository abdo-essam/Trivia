package com.qurio.trivia.utils

object Constants {
    const val QUESTION_TIME_LIMIT = 60000L // 60 seconds
    const val QUESTIONS_PER_GAME = 12

    // Star ratings
    object Stars {
        const val THREE_STARS = 3 // Perfect score
        const val TWO_STARS = 2   // 80%+ correct
        const val ONE_STAR = 1    // 50%+ correct
    }

    // Rewards
    object Rewards {
        const val THREE_STAR_COINS = 100
        const val TWO_STAR_COINS = 50
        const val ONE_STAR_COINS = 25
        const val LOSE_COINS = 0
    }

    // Buy Life
    object BuyLife {
        const val LIFE_COST = 200
    }
}