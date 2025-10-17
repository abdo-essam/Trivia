package com.qurio.trivia.presentation.ui.dialogs.buycharacter.manager

import com.qurio.trivia.databinding.DialogBuyCharacterBinding
import com.qurio.trivia.domain.model.Character
import com.qurio.trivia.presentation.mapper.lockedImageRes

class BuyCharacterUIManager(
    private val binding: DialogBuyCharacterBinding
) {

    fun displayCharacterData(character: Character) {
        binding.apply {
            tvCost.text = character.unlockCost.toString()
            ivCharacter.setImageResource(character.lockedImageRes())
        }
    }

    fun updateBuyButtonState(canAfford: Boolean) {
        binding.btnBuy.apply {
            isEnabled = canAfford
            alpha = if (canAfford) 1.0f else 0.5f
        }
    }

    fun setLoadingState(isLoading: Boolean) {
        binding.apply {
            btnBuy.isEnabled = !isLoading
            btnCancel.isEnabled = !isLoading
        }
    }
}