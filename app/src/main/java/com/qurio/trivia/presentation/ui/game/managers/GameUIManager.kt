package com.qurio.trivia.presentation.ui.game.managers

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import com.qurio.trivia.R
import com.qurio.trivia.data.model.TriviaQuestion
import com.qurio.trivia.databinding.FragmentGameBinding
import com.qurio.trivia.presentation.ui.game.adapter.AnswerOption
import com.qurio.trivia.presentation.ui.game.adapter.AnswerOptionAdapter
import com.qurio.trivia.presentation.ui.game.adapter.AnswerState

class GameUIManager(
    private val binding: FragmentGameBinding,
    private val answerAdapter: AnswerOptionAdapter
) {

    fun displayQuestion(question: TriviaQuestion, questionNumber: Int, totalQuestions: Int) {
        binding.layoutQuestion.root.findViewById<TextView>(R.id.tv_question_counter)?.text =
            binding.root.context.getString(R.string.question_counter, questionNumber, totalQuestions)

        val decodedQuestion = HtmlCompat.fromHtml(
            question.question,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        binding.layoutQuestion.root.findViewById<TextView>(R.id.tv_question)?.text = decodedQuestion
    }

    fun displayAnswerOptions(answers: List<String>) {
        val options = answers.map { text ->
            AnswerOption(text, AnswerState.DEFAULT, true)
        }
        answerAdapter.submitList(options)
    }

    fun updateAnswerSelection(answers: List<String>, selectedIndex: Int) {
        val updatedOptions = answers.mapIndexed { index, text ->
            val state = if (index == selectedIndex) {
                AnswerState.SELECTED
            } else {
                AnswerState.DEFAULT
            }
            AnswerOption(text, state, true)
        }
        answerAdapter.submitList(updatedOptions)
    }

    fun showCorrectAnswer(
        answers: List<String>,
        correctAnswerIndex: Int,
        selectedAnswerIndex: Int,
        isCorrect: Boolean
    ) {
        val updatedOptions = answers.mapIndexed { index, text ->
            when {
                index == correctAnswerIndex -> {
                    AnswerOption(text, AnswerState.CORRECT, false)
                }
                index == selectedAnswerIndex && !isCorrect -> {
                    AnswerOption(text, AnswerState.INCORRECT, false)
                }
                else -> {
                    AnswerOption(text, AnswerState.DEFAULT, false)
                }
            }
        }
        answerAdapter.submitList(updatedOptions)
    }

    fun updateLives(lives: Int) {
        binding.layoutTopBar.tvLives.text = lives.toString()

        val colorRes = when {
            lives == 0 -> R.color.red
            lives <= 1 -> R.color.orange
            else -> R.color.white
        }

        binding.layoutTopBar.tvLives.setTextColor(
            ContextCompat.getColor(binding.root.context, colorRes)
        )
    }

    fun resetForNewQuestion() {
        resetActionButtons()
    }

    fun showNextButton() {
        binding.layoutActionButtons.apply {
            btnCheck.isVisible = false
            btnSkip.isVisible = false
            btnNext.isVisible = true
        }
    }

    fun disableAnswerButtons() {
        binding.layoutActionButtons.apply {
            btnCheck.isEnabled = false
            btnSkip.isEnabled = false
        }
    }

    fun enableCheckButton(enabled: Boolean) {
        binding.layoutActionButtons.btnCheck.isEnabled = enabled
    }

    fun enableAnswersAfterLifePurchase(hasSelection: Boolean) {
        binding.layoutActionButtons.apply {
            btnCheck.isEnabled = hasSelection
            btnSkip.isEnabled = true
        }
    }

    private fun resetActionButtons() {
        binding.layoutActionButtons.apply {
            btnNext.isVisible = false
            btnCheck.isVisible = true
            btnSkip.isVisible = true
            btnCheck.isEnabled = false
            btnSkip.isEnabled = true
        }
    }
}