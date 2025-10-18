package com.qurio.trivia.presentation.ui.game

import com.qurio.trivia.data.model.TriviaQuestion
import com.qurio.trivia.presentation.base.BaseView

interface GameView : BaseView {
    fun displayQuestion(question: TriviaQuestion, questionNumber: Int, totalQuestions: Int)
    fun showCorrectAnswer(correctAnswerIndex: Int, isCorrect: Boolean)
    fun navigateToResults(correctAnswers: Int, incorrectAnswers: Int, skippedAnswers: Int, totalTime: Long)
    fun updateLives(lives: Int)
    fun showOutOfLives()
}