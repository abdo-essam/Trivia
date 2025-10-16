package com.qurio.trivia.presentation.ui.dialogs.difficulty

import android.os.Bundle
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

    private var selectedDifficulty: Difficulty? = null
    private var onDifficultySelectedListener: ((Difficulty) -> Unit)? = null

    private val difficultyButtons by lazy {
        listOf(
            binding.btnDifficultyEasy to Difficulty.EASY,
            binding.btnDifficultyMedium to Difficulty.MEDIUM,
            binding.btnDifficultyHard to Difficulty.HARD
        )
    }

    companion object {
        const val TAG = "DifficultyDialogFragment"

        fun newInstance() = DifficultyDialogFragment()
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
    }

    // ========== Setup ==========

    private fun setupDifficultyButtons() {
        difficultyButtons.forEach { (button, difficulty) ->
            button.setOnClickListener {
                selectDifficulty(difficulty)
            }
        }
    }

    private fun setupActionButtons() {
        binding.apply {
            btnClose.setOnClickListener { dismiss() }
            btnCancel.setOnClickListener { dismiss() }
            btnConfirm.setOnClickListener { confirmSelection() }
        }
    }

    // ========== Difficulty Selection ==========

    private fun selectDifficulty(difficulty: Difficulty) {
        selectedDifficulty = difficulty
        updateDifficultyButtonsState()
        updateConfirmButtonState()
    }

    private fun updateDifficultyButtonsState() {
        difficultyButtons.forEach { (button, difficulty) ->
            button.isSelected = (selectedDifficulty == difficulty)
        }
    }

    private fun updateConfirmButtonState() {
        binding.btnConfirm.isEnabled = (selectedDifficulty != null)
    }

    private fun confirmSelection() {
        selectedDifficulty?.let { difficulty ->
            onDifficultySelectedListener?.invoke(difficulty)
            dismiss()
        }
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