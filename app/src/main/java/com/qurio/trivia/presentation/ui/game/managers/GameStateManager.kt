package com.qurio.trivia.presentation.ui.game.managers

import com.qurio.trivia.data.model.TriviaQuestion

class GameStateManager {
    private var questions: List<TriviaQuestion> = emptyList()
    private var currentQuestionIndex = 0

    var correctAnswers = 0
        private set
    var incorrectAnswers = 0
        private set
    var skippedAnswers = 0
        private set

    var selectedAnswerIndex: Int = NO_SELECTION
        private set

    var currentAnswers: List<String> = emptyList()
        private set

    private var gameStartTime = 0L
    private var currentLives = 0

    fun setQuestions(questions: List<TriviaQuestion>) {
        this.questions = questions
    }

    fun startGame() {
        gameStartTime = System.currentTimeMillis()
        reset()
    }

    fun getCurrentQuestion(): TriviaQuestion? {
        return questions.getOrNull(currentQuestionIndex)
    }

    fun getCurrentQuestionNumber(): Int = currentQuestionIndex + 1

    fun getTotalQuestions(): Int = questions.size

    fun moveToNextQuestion(): Boolean {
        currentQuestionIndex++
        return currentQuestionIndex < questions.size
    }

    fun selectAnswer(index: Int) {
        selectedAnswerIndex = index
    }

    fun setCurrentAnswers(answers: List<String>) {
        currentAnswers = answers
    }

    fun hasSelection(): Boolean = selectedAnswerIndex != NO_SELECTION

    fun resetSelection() {
        selectedAnswerIndex = NO_SELECTION
    }

    fun incrementCorrect() {
        correctAnswers++
    }

    fun incrementIncorrect() {
        incorrectAnswers++
    }

    fun incrementSkipped() {
        skippedAnswers++
    }

    fun setLives(lives: Int) {
        currentLives = lives
    }

    fun decreaseLives(): Int {
        if (currentLives > 0) {
            currentLives--
        }
        return currentLives
    }

    fun hasLives(): Boolean = currentLives > 0

    fun getElapsedTime(): Long {
        return System.currentTimeMillis() - gameStartTime
    }

    private fun reset() {
        currentQuestionIndex = 0
        correctAnswers = 0
        incorrectAnswers = 0
        skippedAnswers = 0
        selectedAnswerIndex = NO_SELECTION
    }

    companion object {
        const val NO_SELECTION = -1
    }
}