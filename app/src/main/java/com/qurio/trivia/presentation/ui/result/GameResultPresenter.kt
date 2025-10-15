package com.qurio.trivia.presentation.ui.result

import android.util.Log
import com.qurio.trivia.domain.model.GameResult
import com.qurio.trivia.domain.repository.AchievementsRepository
import com.qurio.trivia.domain.repository.GameResultRepository
import com.qurio.trivia.presentation.base.BasePresenter
import com.qurio.trivia.utils.Constants
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class GameResultPresenter @Inject constructor(
    private val gameResultRepository: GameResultRepository,
    private val achievementsRepository: AchievementsRepository
) : BasePresenter<GameResultView>() {

    companion object {
        private const val TAG = "GameResultPresenter"
        private const val DATE_FORMAT = "MMM dd, yyyy"
        private const val THREE_STARS_PERCENTAGE = 100f
        private const val TWO_STARS_PERCENTAGE = 80f
        private const val ONE_STAR_PERCENTAGE = 50f
        private const val MAX_SKIPS_FOR_TWO_STARS = 2
    }

    // ========== Save Game Result ==========

    fun saveGameResult(
        category: String,
        correctAnswers: Int,
        incorrectAnswers: Int,
        skippedAnswers: Int,
        timeTaken: Long
    ) {
        val totalQuestions = Constants.QUESTIONS_PER_GAME
        val stars = calculateStars(correctAnswers, skippedAnswers, totalQuestions)
        val coins = calculateCoins(stars)

        Log.d(TAG, "Saving game result: $category, correct=$correctAnswers, stars=$stars, coins=$coins")

        tryToExecute(
            execute = {
                // 1. Create game result
                val gameResult = createGameResult(
                    category = category,
                    totalQuestions = totalQuestions,
                    correctAnswers = correctAnswers,
                    incorrectAnswers = incorrectAnswers,
                    skippedAnswers = skippedAnswers,
                    stars = stars,
                    coins = coins,
                    timeTaken = timeTaken
                )

                // 2. Save game result
                gameResultRepository.saveGameResult(gameResult)

                // 3. Update user coins
                gameResultRepository.updateUserCoins(coins)


                // ✅ 5. Check and unlock achievements
                val newlyUnlocked = achievementsRepository.checkAndUnlockAchievements()

                Log.d(TAG, "Checked achievements: ${newlyUnlocked.size} newly unlocked")

                SaveResult(coins, stars)
            },
            onSuccess = { result ->
                Log.d(TAG, "✓ Game saved: coins=${result.coins}, stars=${result.stars}")
            },
            onError = { error ->
                Log.e(TAG, "✗ Error saving game result", error)
                withView { showError("Failed to save game result") }
            },
            showLoading = false
        )
    }

    // ========== Navigation ==========

    fun playAgain() {
        Log.d(TAG, "Play again requested")
        withView { navigateToPlayAgain() }
    }

    fun backToHome() {
        Log.d(TAG, "Back to home requested")
        withView { navigateToHome() }
    }

    // ========== Star Calculation ==========

    fun calculateStars(correct: Int, skipped: Int, total: Int): Int {
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

    // ========== Coins Calculation ==========

    fun calculateCoins(stars: Int): Int {
        return when (stars) {
            3 -> Constants.Rewards.THREE_STAR_COINS
            2 -> Constants.Rewards.TWO_STAR_COINS
            1 -> Constants.Rewards.ONE_STAR_COINS
            else -> Constants.Rewards.LOSE_COINS
        }
    }

    // ========== Helper Methods ==========

    private fun createGameResult(
        category: String,
        totalQuestions: Int,
        correctAnswers: Int,
        incorrectAnswers: Int,
        skippedAnswers: Int,
        stars: Int,
        coins: Int,
        timeTaken: Long
    ): GameResult {
        return GameResult(
            id = 0L,
            date = getCurrentDate(),
            category = category,
            totalQuestions = totalQuestions,
            correctAnswers = correctAnswers,
            incorrectAnswers = incorrectAnswers,
            skippedAnswers = skippedAnswers,
            stars = stars,
            coins = coins,
            timeTaken = timeTaken,
            timestamp = System.currentTimeMillis()
        )
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        return dateFormat.format(Date())
    }

    // ========== Data Classes ==========

    private data class SaveResult(
        val coins: Int,
        val stars: Int
    )
}