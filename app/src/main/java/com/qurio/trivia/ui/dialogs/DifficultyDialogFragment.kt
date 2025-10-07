package com.qurio.trivia.ui.dialogs

import android.content.res.ColorStateList
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
            // Set default selection (Hard is selected by default in XML)

            // Handle difficulty selection
            btnEasy.setOnClickListener {
                selectDifficulty("easy")
            }

            btnMedium.setOnClickListener {
                selectDifficulty("medium")
            }

            btnHard.setOnClickListener {
                selectDifficulty("hard")
            }

            btnClose.setOnClickListener { dismiss() }
            btnCancel.setOnClickListener { dismiss() }

            btnConfirm.setOnClickListener {
                onDifficultySelected?.invoke(selectedDifficulty)
                dismiss()
            }
        }
    }

    private fun selectDifficulty(difficulty: String) {
        selectedDifficulty = difficulty
        updateButtonStyles()
    }

    private fun updateButtonStyles() {
        binding.apply {
            val context = requireContext()

            // Reset all buttons to unselected state
            btnEasy.backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.disable))
            btnEasy.setTextColor(context.getColor(R.color.shade_secondary))

            btnMedium.backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.disable))
            btnMedium.setTextColor(context.getColor(R.color.shade_secondary))

            btnHard.backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.disable))
            btnHard.setTextColor(context.getColor(R.color.shade_secondary))

            // Highlight selected button
            when (selectedDifficulty) {
                "easy" -> {
                    btnEasy.backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.primary))
                    btnEasy.setTextColor(context.getColor(R.color.white))
                }
                "medium" -> {
                    btnMedium.backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.primary))
                    btnMedium.setTextColor(context.getColor(R.color.white))
                }
                "hard" -> {
                    btnHard.backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.primary))
                    btnHard.setTextColor(context.getColor(R.color.white))
                }
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