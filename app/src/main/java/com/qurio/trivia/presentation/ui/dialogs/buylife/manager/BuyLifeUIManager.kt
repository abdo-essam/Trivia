package com.qurio.trivia.presentation.ui.dialogs.buylife.manager

import com.qurio.trivia.databinding.DialogBuyLifeBinding

class BuyLifeUIManager(
    private val binding: DialogBuyLifeBinding
) {

    fun displayLifeCost(cost: Int) {
        binding.tvCost.text = cost.toString()
    }

    fun updateBuyButtonState(canAfford: Boolean) {
        binding.btnBuy.apply {
            isEnabled = canAfford
            alpha = if (canAfford) 1.0f else 0.5f
        }
    }

    fun setLoadingState(isLoading: Boolean) {
        binding.btnBuy.isEnabled = !isLoading
    }
}