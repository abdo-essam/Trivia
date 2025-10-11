package com.qurio.trivia.presentation.ui.dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.qurio.trivia.databinding.DialogCharacterInfoBinding
import com.qurio.trivia.presentation.base.BaseDialogFragment
import com.qurio.trivia.utils.extensions.loadCharacterImage

class CharacterInfoDialog : BaseDialogFragment() {

    private var _binding: DialogCharacterInfoBinding? = null
    private val binding get() = _binding!!

    // ========== Lifecycle ==========

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCharacterInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupViews() {
        setupClickListeners()
        displayCharacterData()
    }

    // ========== Setup Methods ==========

    private fun setupClickListeners() {
        binding.apply {
            btnClose.setOnClickListener {
                Log.d(TAG, "Close button clicked")
                dismiss()
            }

            btnOk.setOnClickListener {
                Log.d(TAG, "OK button clicked")
                dismiss()
            }
        }
    }

    // ========== Display Data ==========

    private fun displayCharacterData() {
        val characterData = extractArguments() ?: run {
            Log.e(TAG, "Missing character data")
            dismiss()
            return
        }

        with(binding) {
            tvCharacterName.text = characterData.name
            tvCharacterAge.text = characterData.age
            tvCharacterDescription.text = characterData.description
            ivCharacter.loadCharacterImage(characterData.name.lowercase())
        }

        Log.d(TAG, "Displaying character: ${characterData.name}")
    }

    private fun extractArguments(): CharacterData? {
        return arguments?.let { args ->
            CharacterData(
                name = args.getString(ARG_NAME) ?: return null,
                age = args.getString(ARG_AGE) ?: return null,
                description = args.getString(ARG_DESCRIPTION) ?: return null
            )
        }
    }

    // ========== Lifecycle ==========

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ========== Data Classes ==========

    private data class CharacterData(
        val name: String,
        val age: String,
        val description: String
    )

    // ========== Companion Object ==========

    companion object {
        const val TAG = "CharacterInfoDialog"

        private const val ARG_NAME = "name"
        private const val ARG_AGE = "age"
        private const val ARG_DESCRIPTION = "description"

        fun newInstance(
            name: String,
            age: String,
            description: String
        ): CharacterInfoDialog {
            return CharacterInfoDialog().apply {
                arguments = bundleOf(
                    ARG_NAME to name,
                    ARG_AGE to age,
                    ARG_DESCRIPTION to description
                )
            }
        }
    }
}