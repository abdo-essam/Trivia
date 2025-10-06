package com.qurio.trivia.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qurio.trivia.R
import com.qurio.trivia.databinding.DialogDifficultyBinding

class DifficultyDialogFragment : BaseDialogFragment() {

    private var _binding: DialogDifficultyBinding? = null
    private val binding get() = _binding!!

    private var selectedDifficulty: String = "hard"
    private var onDifficultySelected: ((String) -> Unit)? = null

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
            // Set default selection
            toggleDifficulty.check(R.id.btn_hard)

            // Handle difficulty selection
            toggleDifficulty.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (isChecked) {
                    selectedDifficulty = when (checkedId) {
                        R.id.btn_easy -> "easy"
                        R.id.btn_medium -> "medium"
                        R.id.btn_hard -> "hard"
                        else -> "hard"
                    }
                }
            }

            btnClose.setOnClickListener { dismiss() }
            btnCancel.setOnClickListener { dismiss() }

            btnConfirm.setOnClickListener {
                onDifficultySelected?.invoke(selectedDifficulty)
                dismiss()
            }
        }
    }

    fun setOnDifficultySelectedListener(listener: (String) -> Unit) {
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