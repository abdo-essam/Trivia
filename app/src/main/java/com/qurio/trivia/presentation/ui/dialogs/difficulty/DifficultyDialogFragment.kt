package com.qurio.trivia.presentation.ui.dialogs.difficulty

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qurio.trivia.QuriοApp
import com.qurio.trivia.databinding.DialogDifficultyBinding
import com.qurio.trivia.domain.model.Difficulty
import com.qurio.trivia.presentation.base.BaseDialogFragment
import com.qurio.trivia.presentation.ui.dialogs.difficulty.manager.DifficultyUIManager

class DifficultyDialog : BaseDialogFragment() {

    private var _binding: DialogDifficultyBinding? = null
    private val binding get() = _binding!!

    private lateinit var uiManager: DifficultyUIManager
    private var selectedDifficulty: Difficulty? = null
    private var onDifficultySelectedListener: ((Difficulty) -> Unit)? = null

    companion object {
        const val TAG = "DifficultyDialog"
        fun newInstance() = DifficultyDialog()
    }

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
        initializeManagers()
        setupClickListeners()
    }

    private fun initializeManagers() {
        uiManager = DifficultyUIManager(binding)
        uiManager.setupDifficultyButtons(::selectDifficulty)
    }

    private fun setupClickListeners() {
        binding.apply {
            btnClose.setOnClickListener { dismiss() }
            btnCancel.setOnClickListener { dismiss() }
            btnConfirm.setOnClickListener { confirmSelection() }
        }
    }

    private fun selectDifficulty(difficulty: Difficulty) {
        selectedDifficulty = difficulty
        uiManager.updateDifficultySelection(difficulty)
        uiManager.updateConfirmButtonState(true)
    }

    private fun confirmSelection() {
        selectedDifficulty?.let { difficulty ->
            onDifficultySelectedListener?.invoke(difficulty)
            dismiss()
        }
    }

    fun setOnDifficultySelectedListener(listener: (Difficulty) -> Unit) {
        onDifficultySelectedListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}