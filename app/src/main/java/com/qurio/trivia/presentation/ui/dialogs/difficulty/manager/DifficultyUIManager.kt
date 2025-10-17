package com.qurio.trivia.presentation.ui.dialogs.difficulty.manager

import android.widget.Button
import com.qurio.trivia.databinding.DialogDifficultyBinding
import com.qurio.trivia.domain.model.Difficulty

class DifficultyUIManager(
    private val binding: DialogDifficultyBinding
) {

    private val difficultyButtons by lazy {
        listOf(
            binding.btnDifficultyEasy to Difficulty.EASY,
            binding.btnDifficultyMedium to Difficulty.MEDIUM,
            binding.btnDifficultyHard to Difficulty.HARD
        )
    }

    fun setupDifficultyButtons(onDifficultySelected: (Difficulty) -> Unit) {
        difficultyButtons.forEach { (button, difficulty) ->
            button.setOnClickListener {
                onDifficultySelected(difficulty)
            }
        }
    }

    fun updateDifficultySelection(selectedDifficulty: Difficulty?) {
        difficultyButtons.forEach { (button, difficulty) ->
            button.isSelected = (selectedDifficulty == difficulty)
        }
    }

    fun updateConfirmButtonState(isEnabled: Boolean) {
        binding.btnConfirm.isEnabled = isEnabled
    }
}