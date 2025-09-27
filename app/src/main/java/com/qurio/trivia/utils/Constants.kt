package com.qurio.trivia.utils

object Constants {
    const val MAX_LIVES = 4
    const val QUESTIONS_PER_GAME = 12
    const val QUESTION_TIME_LIMIT = 30_000L // 30 seconds

    // Categories from Open Trivia DB
    object Categories {
        const val GENERAL_KNOWLEDGE = 9
        const val SCIENCE_NATURE = 17
        const val GEOGRAPHY = 22
        const val HISTORY = 23
        const val SPORTS = 21
        const val FILM_TV = 11
        const val MUSIC = 12
        const val ART_LITERATURE = 25
        const val FOOD_DRINK = 20
        const val SOCIETY_CULTURE = 27
    }

    // Star ratings
    object Stars {
        const val THREE_STARS = 3 // Perfect score
        const val TWO_STARS = 2   // 80%+ correct
        const val ONE_STAR = 1    // 50%+ correct
    }

    // Rewards
    object Rewards {
        const val THREE_STAR_COINS = 500
        const val TWO_STAR_COINS = 300
        const val ONE_STAR_COINS = 100
        const val LOSE_COINS = 1
    }
}