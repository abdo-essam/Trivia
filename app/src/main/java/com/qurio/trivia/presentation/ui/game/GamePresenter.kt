package com.qurio.trivia.presentation.ui.game

import android.util.Log
import com.qurio.trivia.presentation.base.BasePresenter
import com.qurio.trivia.data.database.dao.UserProgressDao
import com.qurio.trivia.domain.model.Difficulty
import com.qurio.trivia.data.model.TriviaQuestion
import com.qurio.trivia.domain.repository.TriviaRepository
import com.qurio.trivia.utils.Constants
import javax.inject.Inject

class GamePresenter @Inject constructor(
    private val triviaRepository: TriviaRepository,
    private val userProgressDao: UserProgressDao
) : BasePresenter<GameView>() {

    // ========== Game State ==========

    private var questions: List<TriviaQuestion> = emptyList()
    private var currentQuestionIndex = 0
    private var correctAnswers = 0
    private var incorrectAnswers = 0
    private var skippedAnswers = 0
    private var gameStartTime = 0L

    // ========== Load Questions ==========

    fun loadQuestions(categoryId: Int, difficulty: Difficulty) {
        gameStartTime = System.currentTimeMillis()
        resetGameState()
        tryToExecute(
            execute = {
                triviaRepository.getQuestions(
                    amount = Constants.QUESTIONS_PER_GAME,
                    category = categoryId,
                    difficulty = difficulty.value
                )
            },
            onSuccess = { result ->
                result.onSuccess { triviaResponse ->
                    handleQuestionsLoaded(triviaResponse.results)
                }.onFailure { exception ->
                    Log.e(TAG, "Failed to load questions", exception)
                    withView { showNoConnection() }
                }
            },
            onError = { error ->
                Log.e(TAG, "Error loading questions", error)
                withView { showNoConnection() }
            },
            showLoading = true
        )
    }

    private fun handleQuestionsLoaded(loadedQuestions: List<TriviaQuestion>) {
        questions = loadedQuestions

        if (questions.isNotEmpty()) {
            showCurrentQuestion()
            updateUserStats()
        } else {
            withView { showError("No questions available for this category") }
        }
    }

    // ========== Answer Handling ==========

    fun submitAnswer(selectedAnswerIndex: Int) {
        if (!isValidQuestionIndex()) return

        val currentQuestion = getCurrentQuestion() ?: return
        val allAnswers = currentQuestion.getAllAnswers()
        val selectedAnswer = allAnswers.getOrNull(selectedAnswerIndex) ?: return
        val correctAnswerIndex = allAnswers.indexOf(currentQuestion.correctAnswer)

        val isCorrect = selectedAnswer == currentQuestion.correctAnswer

        updateAnswerStats(isCorrect)
        withView { showCorrectAnswer(correctAnswerIndex, isCorrect) }
    }

    fun skipQuestion() {
        if (!isValidQuestionIndex()) return

        skippedAnswers++

        val currentQuestion = getCurrentQuestion() ?: return
        val allAnswers = currentQuestion.getAllAnswers()
        val correctAnswerIndex = allAnswers.indexOf(currentQuestion.correctAnswer)

        withView { showCorrectAnswer(correctAnswerIndex, isCorrect = false) }
    }

    fun timeUp() {
        skipQuestion()
    }

    private fun updateAnswerStats(isCorrect: Boolean) {
        if (isCorrect) {
            correctAnswers++
        } else {
            incorrectAnswers++
        }
    }

    // ========== Question Navigation ==========

    fun nextQuestion() {
        currentQuestionIndex++

        if (currentQuestionIndex < questions.size) {
            showCurrentQuestion()
        } else {
            endGame()
        }
    }

    private fun showCurrentQuestion() {
        val question = getCurrentQuestion() ?: return
            withView {
                displayQuestion(
                    question = question,
                    questionNumber = currentQuestionIndex + 1,
                    totalQuestions = questions.size
                )
            }
    }

    // ========== Game End ==========

    private fun endGame() {
        val totalTime = System.currentTimeMillis() - gameStartTime

        withView {
            navigateToResults(
                correctAnswers = correctAnswers,
                incorrectAnswers = incorrectAnswers,
                skippedAnswers = skippedAnswers,
                totalTime = totalTime
            )
        }

        saveGameResult(totalTime)
    }

    private fun saveGameResult(totalTime: Long) {
        tryToExecute(
            execute = {
                val gameStats = calculateGameStats()
                val userProgress = userProgressDao.getUserProgress()

                userProgress?.let {
                    val newTotalCoins = it.totalCoins + gameStats.coinsEarned
                    userProgressDao.updateCoins(newTotalCoins)
                }

                gameStats
            },
            onSuccess = { stats ->
                Log.d(TAG, "Game saved: ${stats.stars} stars, ${stats.coinsEarned} coins earned")
            },
            onError = { error ->
                Log.e(TAG, "Failed to save game result", error)
            },
            showLoading = false
        )
    }

    private fun calculateGameStats(): GameStats {
        val totalQuestions = Constants.QUESTIONS_PER_GAME
        val correctPercentage = (correctAnswers.toFloat() / totalQuestions) * 100

        val stars = when {
            correctAnswers == totalQuestions && skippedAnswers == 0 -> Constants.Stars.THREE_STARS
            correctPercentage >= PERCENTAGE_TWO_STARS && skippedAnswers <= MAX_SKIPS_TWO_STARS -> Constants.Stars.TWO_STARS
            correctPercentage >= PERCENTAGE_ONE_STAR -> Constants.Stars.ONE_STAR
            else -> 0
        }

        val coinsEarned = when (stars) {
            3 -> Constants.Rewards.THREE_STAR_COINS
            2 -> Constants.Rewards.TWO_STAR_COINS
            1 -> Constants.Rewards.ONE_STAR_COINS
            else -> Constants.Rewards.LOSE_COINS
        }

        return GameStats(stars, coinsEarned, correctPercentage.toInt())
    }

    // ========== User Stats ==========

    private fun updateUserStats() {
        tryToExecute(
            execute = {
                userProgressDao.getUserProgress()
            },
            onSuccess = { userProgress ->
                userProgress?.let {
                    withView { updateStats(it.lives, it.totalCoins) }
                }
            },
            onError = { error ->
                Log.e(TAG, "Failed to update user stats", error)
            },
            showLoading = false
        )
    }

    // ========== Helper Methods ==========

    private fun isValidQuestionIndex(): Boolean {
        return currentQuestionIndex in questions.indices
    }

    private fun getCurrentQuestion(): TriviaQuestion? {
        return questions.getOrNull(currentQuestionIndex)
    }

    private fun resetGameState() {
        currentQuestionIndex = 0
        correctAnswers = 0
        incorrectAnswers = 0
        skippedAnswers = 0
    }

    // ========== Data Classes ==========

    private data class GameStats(
        val stars: Int,
        val coinsEarned: Int,
        val percentage: Int
    )

    // ========== Constants ==========

    companion object {
        private const val TAG = "GamePresenter"
        private const val PERCENTAGE_TWO_STARS = 80f
        private const val PERCENTAGE_ONE_STAR = 50f
        private const val MAX_SKIPS_TWO_STARS = 2
    }
}