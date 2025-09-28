package com.qurio.trivia.ui.game

import com.qurio.trivia.base.BaseView
import com.qurio.trivia.data.model.TriviaQuestion

interface GameView : BaseView {
    fun displayQuestion(question: TriviaQuestion, questionNumber: Int, totalQuestions: Int)
    fun displayQuestionWithImage(question: TriviaQuestion, imageUrl: String?, questionNumber: Int, totalQuestions: Int)
    fun showCorrectAnswer(correctAnswerIndex: Int)
    fun navigateToResults(correctAnswers: Int, incorrectAnswers: Int, skippedAnswers: Int, totalTime: Long)
    fun updateStats(lives: Int, coins: Int)
}