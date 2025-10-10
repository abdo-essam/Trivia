package com.qurio.trivia.presentation.ui.game

import com.qurio.trivia.base.BasePresenter
import com.qurio.trivia.data.database.UserProgressDao
import com.qurio.trivia.data.model.Difficulty
import com.qurio.trivia.data.model.TriviaQuestion
import com.qurio.trivia.data.repository.TriviaRepository
import com.qurio.trivia.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class GamePresenter @Inject constructor(
    private val triviaRepository: TriviaRepository,
    private val userProgressDao: UserProgressDao
) : BasePresenter<GameView>() {

    private var questions: List<TriviaQuestion> = emptyList()
    private var currentQuestionIndex = 0
    private var correctAnswers = 0
    private var incorrectAnswers = 0
    private var skippedAnswers = 0
    private var gameStartTime = 0L
    private var hasShownImageInSession = false // Track if image was shown

    fun loadQuestions(categoryId: Int, difficulty: Difficulty) {
        view?.showLoading()
        gameStartTime = System.currentTimeMillis()
        hasShownImageInSession = false // Reset for new game

        CoroutineScope(Dispatchers.IO).launch {
            val result = triviaRepository.getQuestions(
                Constants.QUESTIONS_PER_GAME,
                categoryId,
                difficulty.value
            )

            CoroutineScope(Dispatchers.Main).launch {
                view?.hideLoading()

                result.onSuccess { triviaResponse ->
                    questions = triviaResponse.results
                    currentQuestionIndex = 0
                    correctAnswers = 0
                    incorrectAnswers = 0
                    skippedAnswers = 0

                    if (questions.isNotEmpty()) {
                        showCurrentQuestion()
                        updateUserStats()
                    } else {
                        view?.showError("No questions available")
                    }
                }.onFailure {
                    view?.showNoConnection()
                }
            }
        }
    }

    fun submitAnswer(selectedAnswerIndex: Int) {
        if (currentQuestionIndex >= questions.size) return

        val currentQuestion = questions[currentQuestionIndex]
        val allAnswers = currentQuestion.getAllAnswers()
        val selectedAnswer = allAnswers[selectedAnswerIndex]
        val correctAnswerIndex = allAnswers.indexOf(currentQuestion.correctAnswer)

        val isCorrect = selectedAnswer == currentQuestion.correctAnswer

        if (isCorrect) {
            correctAnswers++
        } else {
            incorrectAnswers++
        }

        view?.showCorrectAnswer(correctAnswerIndex, isCorrect)
    }

    fun skipQuestion() {
        if (currentQuestionIndex >= questions.size) return

        skippedAnswers++
        val currentQuestion = questions[currentQuestionIndex]
        val allAnswers = currentQuestion.getAllAnswers()
        val correctAnswerIndex = allAnswers.indexOf(currentQuestion.correctAnswer)

        view?.showCorrectAnswer(correctAnswerIndex, false)
    }

    fun timeUp() {
        skipQuestion()
    }

    fun nextQuestion() {
        currentQuestionIndex++

        if (currentQuestionIndex < questions.size) {
            showCurrentQuestion()
        } else {
            endGame()
        }
    }

    private fun showCurrentQuestion() {
        if (currentQuestionIndex < questions.size) {
            val question = questions[currentQuestionIndex]

            // Show image only once per game session and only for specific categories
            val canShowImage = !hasShownImageInSession &&
                    (question.category.contains("Art", ignoreCase = true) ||
                            question.category.contains("Film", ignoreCase = true) ||
                            question.category.contains("Science", ignoreCase = true))

            if (canShowImage && Math.random() > 0.7) { // 30% chance
                hasShownImageInSession = true
                view?.displayQuestionWithImage(
                    question,
                    "https://picsum.photos/600/400",
                    currentQuestionIndex + 1,
                    questions.size
                )
            } else {
                view?.displayQuestion(
                    question,
                    currentQuestionIndex + 1,
                    questions.size
                )
            }
        }
    }

    private fun endGame() {
        val totalTime = System.currentTimeMillis() - gameStartTime
        view?.navigateToResults(correctAnswers, incorrectAnswers, skippedAnswers, totalTime)
        saveGameResult(totalTime)
    }

    private fun updateUserStats() {
        CoroutineScope(Dispatchers.IO).launch {
            val userProgress = userProgressDao.getUserProgress()
            userProgress?.let {
                CoroutineScope(Dispatchers.Main).launch {
                    view?.updateStats(it.lives, it.totalCoins)
                }
            }
        }
    }

    private fun saveGameResult(totalTime: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val totalQuestions = Constants.QUESTIONS_PER_GAME
            val correctPercentage = (correctAnswers.toFloat() / totalQuestions) * 100

            val stars = when {
                correctAnswers == totalQuestions && skippedAnswers == 0 -> Constants.Stars.THREE_STARS
                correctPercentage >= 80 && skippedAnswers <= 2 -> Constants.Stars.TWO_STARS
                correctPercentage >= 50 -> Constants.Stars.ONE_STAR
                else -> 0
            }

            val coinsEarned = when (stars) {
                3 -> Constants.Rewards.THREE_STAR_COINS
                2 -> Constants.Rewards.TWO_STAR_COINS
                1 -> Constants.Rewards.ONE_STAR_COINS
                else -> Constants.Rewards.LOSE_COINS
            }

            val userProgress = userProgressDao.getUserProgress()
            userProgress?.let {
                val newTotalCoins = it.totalCoins + coinsEarned
                userProgressDao.updateCoins(newTotalCoins)
            }
        }
    }
}