package com.qurio.trivia.presentation.ui.dialogs.difficulty

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.databinding.DialogDifficultyBinding
import com.qurio.trivia.domain.model.Difficulty
import com.qurio.trivia.presentation.base.BaseDialogFragment

class DifficultyDialogFragment : BaseDialogFragment() {

    private var _binding: DialogDifficultyBinding? = null
    private val binding get() = _binding!!


    private var selectedDifficulty: Difficulty = Difficulty.HARD
    private var onDifficultySelectedListener: ((Difficulty) -> Unit)? = null

    companion object {
        const val TAG = "DifficultyDialogFragment"

        fun newInstance(): DifficultyDialogFragment {
            return DifficultyDialogFragment()
        }
    }

    // ========== Lifecycle ==========

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as QuriοApp).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogDifficultyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupViews() {
        setupDifficultyButtons()
        setupActionButtons()

        // Set default selection to HARD
        updateButtonStyles()
    }

    // ========== Setup Methods ==========

    private fun setupDifficultyButtons() {
        binding.apply {
            btnEasy.setOnClickListener {

                Log.d(TAG, "Easy difficulty selected")
                selectDifficulty(Difficulty.EASY)
            }
            btnMedium.setOnClickListener {
                Log.d(TAG, "Medium difficulty selected")
                selectDifficulty(Difficulty.MEDIUM)
            }

            btnHard.setOnClickListener {

                Log.d(TAG, "Hard difficulty selected")
                selectDifficulty(Difficulty.HARD)
            }
        }
    }

    private fun setupActionButtons() {

        binding.apply {
            btnClose.setOnClickListener {

                Log.d(TAG, "Close button clicked")
                dismiss()
            }

            btnCancel.setOnClickListener {

                Log.d(TAG, "Cancel button clicked")
                dismiss()
            }

            btnConfirm.setOnClickListener {

                Log.d(TAG, "Confirm clicked with difficulty: ${selectedDifficulty.displayName}")
                handleConfirm()
            }
        }
    }

    // ========== Difficulty Selection ==========

    private fun selectDifficulty(difficulty: Difficulty) {
        selectedDifficulty = difficulty
        updateButtonStyles()
    }

    private fun updateButtonStyles() {
        binding.apply {
            // Reset all buttons to unselected state
            btnEasy.isSelected = false
            btnMedium.isSelected = false
            btnHard.isSelected = false

            // Highlight selected button
            when (selectedDifficulty) {
                Difficulty.EASY -> btnEasy.isSelected = true
                Difficulty.MEDIUM -> btnMedium.isSelected = true
                Difficulty.HARD -> btnHard.isSelected = true
            }
        }
    }

    // ========== Actions ==========

    private fun handleConfirm() {
        onDifficultySelectedListener?.invoke(selectedDifficulty)
        dismiss()
    }

    // ========== Public API ==========

    fun setOnDifficultySelectedListener(listener: (Difficulty) -> Unit) {
        onDifficultySelectedListener = listener
    }

    // ========== Lifecycle ==========

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}