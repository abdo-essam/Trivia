package com.qurio.trivia.presentation.ui.game

import com.qurio.trivia.data.model.TriviaQuestion
import com.qurio.trivia.domain.model.Difficulty
import com.qurio.trivia.domain.repository.TriviaRepository
import com.qurio.trivia.domain.repository.UserRepository
import com.qurio.trivia.presentation.base.BasePresenter
import com.qurio.trivia.presentation.ui.game.managers.GameStateManager
import com.qurio.trivia.utils.Constants
import javax.inject.Inject

class GamePresenter @Inject constructor(
    private val triviaRepository: TriviaRepository,
    private val userRepository: UserRepository
) : BasePresenter<GameView>() {

    private val gameState = GameStateManager()

    fun loadQuestions(categoryId: Int, difficulty: Difficulty) {
        gameState.startGame()

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
                }.onFailure {
                    withView { showNoConnection() }
                }
            },
            onError = {
                withView { showNoConnection() }
            },
            showLoading = true
        )
    }

    private fun handleQuestionsLoaded(loadedQuestions: List<TriviaQuestion>) {
        if (loadedQuestions.isEmpty()) {
            withView { showError("No questions available for this category") }
            return
        }

        gameState.setQuestions(loadedQuestions)
        showCurrentQuestion()
        loadUserLives()
    }

    fun submitAnswer(selectedAnswerIndex: Int) {
        val currentQuestion = gameState.getCurrentQuestion() ?: return
        val allAnswers = currentQuestion.getAllAnswers()
        val selectedAnswer = allAnswers.getOrNull(selectedAnswerIndex) ?: return
        val correctAnswerIndex = allAnswers.indexOf(currentQuestion.correctAnswer)

        val isCorrect = selectedAnswer == currentQuestion.correctAnswer

        if (isCorrect) {
            gameState.incrementCorrect()
        } else {
            gameState.incrementIncorrect()
            handleLifeDecrease()
        }

        withView { showCorrectAnswer(correctAnswerIndex, isCorrect) }
    }

    fun skipQuestion() {
        gameState.incrementSkipped()

        val currentQuestion = gameState.getCurrentQuestion() ?: return
        val allAnswers = currentQuestion.getAllAnswers()
        val correctAnswerIndex = allAnswers.indexOf(currentQuestion.correctAnswer)

        withView { showCorrectAnswer(correctAnswerIndex, isCorrect = false) }
    }

    fun timeUp() {
        gameState.incrementIncorrect()
        handleLifeDecrease()

        val currentQuestion = gameState.getCurrentQuestion() ?: return
        val allAnswers = currentQuestion.getAllAnswers()
        val correctAnswerIndex = allAnswers.indexOf(currentQuestion.correctAnswer)

        withView { showCorrectAnswer(correctAnswerIndex, isCorrect = false) }
    }

    private fun handleLifeDecrease() {
        val newLives = gameState.decreaseLives()
        updateLivesInDatabase(newLives)
        withView { updateLives(newLives) }

        if (!gameState.hasLives()) {
            withView { showOutOfLives() }
        }
    }

    fun nextQuestion() {
        if (!gameState.hasLives()) return

        if (gameState.moveToNextQuestion()) {
            showCurrentQuestion()
        } else {
            endGame()
        }
    }

    private fun showCurrentQuestion() {
        val question = gameState.getCurrentQuestion() ?: return
        withView {
            displayQuestion(
                question = question,
                questionNumber = gameState.getCurrentQuestionNumber(),
                totalQuestions = gameState.getTotalQuestions()
            )
        }
    }

    private fun endGame() {
        val totalTime = gameState.getElapsedTime()

        withView {
            navigateToResults(
                correctAnswers = gameState.correctAnswers,
                incorrectAnswers = gameState.incorrectAnswers,
                skippedAnswers = gameState.skippedAnswers,
                totalTime = totalTime
            )
        }

        saveGameResult()
    }

    private fun saveGameResult() {
        tryToExecute(
            execute = {
                val gameScore = calculateGameScore(
                    correct = gameState.correctAnswers,
                    skipped = gameState.skippedAnswers
                )

                val userProgress = userRepository.getUserProgress()
                userProgress?.let {
                    val newTotalCoins = it.totalCoins + gameScore.coinsEarned
                    userRepository.updateCoins(newTotalCoins)
                }

                gameScore
            },
            onSuccess = {},
            onError = {},
            showLoading = false
        )
    }

    private fun calculateGameScore(correct: Int, skipped: Int): GameScore {
        val totalQuestions = Constants.QUESTIONS_PER_GAME
        val correctPercentage = (correct.toFloat() / totalQuestions) * 100

        val stars = calculateStars(correct, skipped, correctPercentage, totalQuestions)
        val coinsEarned = calculateCoins(stars)

        return GameScore(stars, coinsEarned, correctPercentage.toInt())
    }

    private fun calculateStars(
        correct: Int,
        skipped: Int,
        correctPercentage: Float,
        totalQuestions: Int
    ): Int {
        return when {
            correct == totalQuestions && skipped == 0 -> Constants.Stars.THREE_STARS
            correctPercentage >= PERCENTAGE_TWO_STARS && skipped <= MAX_SKIPS_TWO_STARS ->
                Constants.Stars.TWO_STARS
            correctPercentage >= PERCENTAGE_ONE_STAR -> Constants.Stars.ONE_STAR
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

    private fun loadUserLives() {
        tryToExecute(
            execute = {
                userRepository.getUserProgress()
            },
            onSuccess = { userProgress ->
                userProgress?.let {
                    gameState.setLives(it.lives)
                    withView { updateLives(it.lives) }
                }
            },
            onError = {},
            showLoading = false
        )
    }

    fun refreshLives() {
        loadUserLives()
    }

    private fun updateLivesInDatabase(lives: Int) {
        tryToExecute(
            execute = {
                userRepository.updateLives(lives)
            },
            onSuccess = {},
            onError = {},
            showLoading = false
        )
    }

    private data class GameScore(
        val stars: Int,
        val coinsEarned: Int,
        val percentage: Int
    )

    companion object {
        private const val PERCENTAGE_TWO_STARS = 80f
        private const val PERCENTAGE_ONE_STAR = 50f
        private const val MAX_SKIPS_TWO_STARS = 2
    }
}