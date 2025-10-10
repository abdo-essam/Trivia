package com.qurio.trivia.presentation.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.qurio.trivia.data.model.Character
import com.qurio.trivia.databinding.DialogBuyCharacterBinding

class BuyCharacterDialog : BaseDialogFragment() {

    private var _binding: DialogBuyCharacterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogBuyCharacterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        loadCharacterData()
    }

    private fun setupViews() {
        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnBuy.setOnClickListener {
            // TODO: Implement purchase logic
            // Check if user has enough coins
            // Deduct coins and unlock character
            dismiss()
        }
    }

    private fun loadCharacterData() {
        val characterName = arguments?.getString(ARG_CHARACTER_NAME) ?: return
        val cost = arguments?.getInt(ARG_COST) ?: return
        val lockedImageRes = arguments?.getInt(ARG_IMAGE_RES) ?: return

        binding.apply {
            tvCost.text = cost.toString()
            ivCharacter.setImageResource(lockedImageRes)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "BuyCharacterDialog"
        private const val ARG_CHARACTER_NAME = "character_name"
        private const val ARG_COST = "cost"
        private const val ARG_IMAGE_RES = "image_res"

        fun newInstance(character: Character) = BuyCharacterDialog().apply {
            arguments = bundleOf(
                ARG_CHARACTER_NAME to character.name,
                ARG_COST to character.unlockCost,
                ARG_IMAGE_RES to character.lockedImageRes
            )
        }
    }
}