package com.qurio.trivia.presentation.ui.result

import com.qurio.trivia.domain.model.GameResult
import com.qurio.trivia.domain.repository.AchievementsRepository
import com.qurio.trivia.domain.repository.GameResultRepository
import com.qurio.trivia.domain.repository.UserRepository
import com.qurio.trivia.presentation.base.BasePresenter
import com.qurio.trivia.utils.DateUtils
import javax.inject.Inject

class GameResultPresenter @Inject constructor(
    private val gameResultRepository: GameResultRepository,
    private val userRepository: UserRepository,
    private val achievementsRepository: AchievementsRepository
) : BasePresenter<GameResultView>() {

    fun saveGameResult(
        category: String,
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
                    correctAnswers = correctAnswers,
                    incorrectAnswers = incorrectAnswers,
                    skippedAnswers = skippedAnswers,
                    stars = stars,
                    coins = coins,
                    timeTaken = timeTaken
                )

                gameResultRepository.saveGameResult(gameResult)
                updateUserCoinsAndAchievements(coins)
            },
            onSuccess = {},
            onError = {
                withView { showError("Failed to save game result") }
            },
            showLoading = false
        )
    }

    fun playAgain() {
        withView { navigateToPlayAgain() }
    }

    fun backToHome() {
        withView { navigateToHome() }
    }

    private suspend fun updateUserCoinsAndAchievements(coins: Int) {
        val userProgress = userRepository.getUserProgress()
        userProgress?.let {
            val newTotalCoins = it.totalCoins + coins
            userRepository.updateCoins(newTotalCoins)
        }
        achievementsRepository.checkAndUnlockAchievements()
    }

    private fun createGameResult(
        category: String,
        correctAnswers: Int,
        incorrectAnswers: Int,
        skippedAnswers: Int,
        stars: Int,
        coins: Int,
        timeTaken: Long
    ): GameResult {
        return GameResult(
            id = 0L,
            date = DateUtils.getCurrentFormattedDate(),
            category = category,
            totalQuestions = correctAnswers + incorrectAnswers + skippedAnswers,
            correctAnswers = correctAnswers,
            incorrectAnswers = incorrectAnswers,
            skippedAnswers = skippedAnswers,
            stars = stars,
            coins = coins,
            timeTaken = timeTaken,
            timestamp = System.currentTimeMillis()
        )
    }
}