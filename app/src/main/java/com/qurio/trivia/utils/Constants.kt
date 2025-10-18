package com.qurio.trivia.utils

object Constants {
    const val QUESTION_TIME_LIMIT = 60000L
    const val QUESTIONS_PER_GAME = 12

    object Stars {
        const val THREE_STARS = 3
        const val TWO_STARS = 2
        const val ONE_STAR = 1
    }

    object Rewards {
        const val THREE_STAR_COINS = 100
        const val TWO_STAR_COINS = 50
        const val ONE_STAR_COINS = 25
        const val LOSE_COINS = 0
    }

    object BuyLife {
        const val TAG = "BuyLifeDialog"
        const val LIFE_COST = 200
    }

    object Sound {
        const val SOUND_CORRECT = 1
        const val SOUND_WRONG = 2
        const val SOUND_COINS = 3
        const val SOUND_DIALOG_OPEN = 4
        const val SOUND_DIALOG_CLOSE = 5
    }

    object Settings {
        const val TAG = "SettingsDialog"
        const val KEY_SOUND_VOLUME = "sound_volume"
        const val DEFAULT_VOLUME = 80f
        const val MIN_VOLUME = 0f
        const val MAX_VOLUME = 100f
    }
}