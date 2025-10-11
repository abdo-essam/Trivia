package com.qurio.trivia.presentation.ui.dialogs.buycharacter

import com.qurio.trivia.presentation.base.BaseView

interface BuyCharacterView : BaseView {
    fun updateUserCoins(coins: Int)
    fun onPurchaseSuccess(characterName: String, remainingCoins: Int)
    fun showInsufficientCoins(currentCoins: Int, requiredCoins: Int)
}