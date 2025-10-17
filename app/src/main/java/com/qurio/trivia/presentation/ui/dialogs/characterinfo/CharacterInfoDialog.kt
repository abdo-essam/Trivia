package com.qurio.trivia.presentation.ui.dialogs.characterinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.qurio.trivia.databinding.DialogCharacterInfoBinding
import com.qurio.trivia.domain.model.Character
import com.qurio.trivia.presentation.base.BaseDialogFragment
import com.qurio.trivia.presentation.ui.dialogs.characterinfo.manager.CharacterInfoUIManager

class CharacterInfoDialog : BaseDialogFragment() {

    private var _binding: DialogCharacterInfoBinding? = null
    private val binding get() = _binding!!

    private lateinit var uiManager: CharacterInfoUIManager
    private lateinit var character: Character
    private var onConfirmListener: (() -> Unit)? = null

    companion object {
        const val TAG = "CharacterInfoDialog"
        private const val ARG_CHARACTER_NAME = "character_name"

        fun newInstance(character: Character): CharacterInfoDialog {
            return CharacterInfoDialog().apply {
                arguments = bundleOf(ARG_CHARACTER_NAME to character.characterName)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val characterName = arguments?.getString(ARG_CHARACTER_NAME)
        character = Character.fromName(characterName ?: "") ?: Character.default()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCharacterInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupViews() {
        initializeManagers()
        setupClickListeners()
    }

    private fun initializeManagers() {
        uiManager = CharacterInfoUIManager(binding)
        uiManager.displayCharacterInfo(character)
    }

    private fun setupClickListeners() {
        binding.apply {
            btnClose.setOnClickListener { dismiss() }
            btnOk.setOnClickListener {
                onConfirmListener?.invoke()
                dismiss()
            }
        }
    }

    fun setOnConfirmListener(listener: () -> Unit) {
        onConfirmListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}