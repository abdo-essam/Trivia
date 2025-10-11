package com.qurio.trivia.presentation.ui.result

import android.util.Log
import com.qurio.trivia.presentation.base.BasePresenter
import com.qurio.trivia.data.model.GameResult
import com.qurio.trivia.data.repository.GameResultRepository
import javax.inject.Inject

class GameResultPresenter @Inject constructor(
    private val repository: GameResultRepository
) : BasePresenter<GameResultView>() {

    // ========== Save Game Result ==========

    fun saveGameResult(
        category: String,
        totalQuestions: Int,
        correctAnswers: Int,
        incorrectAnswers: Int,
        skippedAnswers: Int,
        stars: Int,
        coins: Int,
        timeTaken: Long
    ) {
        tryToExecute(
            execute = {
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

                repository.saveGameResult(gameResult)
                repository.updateUserCoins(coins)
                if (stars > 0) {
                    repository.updateUserAwards(stars)
                }

                SaveResult(gameResult.id, coins, stars)
            },
            onSuccess = { result ->
                Log.d(TAG, "Game saved: ID=${result.gameId}, Coins=${result.coins}, Stars=${result.stars}")
                withView { onGameResultSaved(result.coins, result.stars) }
            },
            onError = { error ->
                Log.e(TAG, "Error saving game result", error)
                // Don't show error to user, just log it
            },
            showLoading = false
        )
    }

    // ========== Navigation ==========

    fun playAgain() {
        withView { navigateToPlayAgain() }
    }

    fun backToHome() {
        withView { navigateToHome() }
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
        val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
        return dateFormat.format(java.util.Date())
    }

    // ========== Data Classes ==========

    private data class SaveResult(
        val gameId: Long,
        val coins: Int,
        val stars: Int
    )

    companion object {
        private const val TAG = "GameResultPresenter"
    }
}