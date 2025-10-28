package com.qurio.trivia.presentation.ui.result.managers

import com.qurio.trivia.presentation.ui.result.model.GameResultStats
import com.qurio.trivia.utils.Constants

class GameResultCalculator {

    fun calculateGameStats(
        correctAnswers: Int,
        incorrectAnswers: Int,
        skippedAnswers: Int
    ): GameResultStats {
        val totalQuestions = Constants.QUESTIONS_PER_GAME
        val stars = calculateStars(correctAnswers, skippedAnswers, totalQuestions)
        val coins = calculateCoins(stars)
        val percentage = calculatePercentage(correctAnswers, totalQuestions)

        return GameResultStats(
            correct = correctAnswers,
            incorrect = incorrectAnswers,
            skipped = skippedAnswers,
            stars = stars,
            coins = coins,
            isWon = stars > 0,
            percentage = percentage
        )
    }

    private fun calculateStars(correct: Int, skipped: Int, total: Int): Int {
        val correctPercentage = if (total > 0) {
            (correct.toFloat() / total) * 100
        } else 0f

        return when {
            correct == total && skipped == 0 -> Constants.Stars.THREE_STARS
            correctPercentage >= TWO_STARS_PERCENTAGE && skipped <= MAX_SKIPS_FOR_TWO_STARS ->
                Constants.Stars.TWO_STARS
            correctPercentage >= ONE_STAR_PERCENTAGE -> Constants.Stars.ONE_STAR
            else -> 0
        }
    }

    private fun calculateCoins(stars: Int): Int {
        return when (stars) {
            3 -> Constants.Rewards.THREE_STAR_COINS
            2 -> Constants.Rewards.TWO_STAR_COINS
            1 -> Constants.Rewards.ONE_STAR_COINS
            else -> Constants.Rewards.LOSE_COINS
        }
    }

    private fun calculatePercentage(correct: Int, total: Int): Int {
        return if (total > 0) {
            ((correct.toFloat() / total) * 100).toInt()
        } else 0
    }

    companion object {
        private const val TWO_STARS_PERCENTAGE = 80f
        private const val ONE_STAR_PERCENTAGE = 50f
        private const val MAX_SKIPS_FOR_TWO_STARS = 2
    }
}