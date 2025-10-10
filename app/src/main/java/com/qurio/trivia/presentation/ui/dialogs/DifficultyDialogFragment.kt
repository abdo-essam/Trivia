package com.qurio.trivia.presentation.ui.dialogs

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qurio.trivia.R
import com.qurio.trivia.data.model.Difficulty
import com.qurio.trivia.databinding.DialogDifficultyBinding

class DifficultyDialogFragment : BaseDialogFragment() {

    private var _binding: DialogDifficultyBinding? = null
    private val binding get() = _binding!!

    private var selectedDifficulty: Difficulty = Difficulty.HARD
    private var onDifficultySelected: ((Difficulty) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogDifficultyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        binding.apply {
            // Set button texts from enum
            btnEasy.text = Difficulty.EASY.displayName
            btnMedium.text = Difficulty.MEDIUM.displayName
            btnHard.text = Difficulty.HARD.displayName

            // Default selection is HARD
            updateButtonStyles()

            // Handle difficulty selection
            btnEasy.setOnClickListener {
                selectDifficulty(Difficulty.EASY)
            }

            btnMedium.setOnClickListener {
                selectDifficulty(Difficulty.MEDIUM)
            }

            btnHard.setOnClickListener {
                selectDifficulty(Difficulty.HARD)
            }

            btnClose.setOnClickListener { dismiss() }
            btnCancel.setOnClickListener { dismiss() }

            btnConfirm.setOnClickListener {
                onDifficultySelected?.invoke(selectedDifficulty)
                dismiss()
            }
        }
    }

    private fun selectDifficulty(difficulty: Difficulty) {
        selectedDifficulty = difficulty
        updateButtonStyles()
    }

    private fun updateButtonStyles() {
        binding.apply {
            // Reset all buttons
            btnEasy.isSelected = false
            btnMedium.isSelected = false
            btnHard.isSelected = false

            // Set selected button
            when (selectedDifficulty) {
                Difficulty.EASY -> btnEasy.isSelected = true
                Difficulty.MEDIUM -> btnMedium.isSelected = true
                Difficulty.HARD -> btnHard.isSelected = true
            }
        }
    }

    fun setOnDifficultySelectedListener(listener: (Difficulty) -> Unit) {
        onDifficultySelected = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "DifficultyDialogFragment"
    }
}