package com.qurio.trivia.presentation.ui.dialogs.buylife

import com.qurio.trivia.presentation.base.BaseView

interface BuyLifeView : BaseView {
    fun updateUserCoins(coins: Int)
    fun onPurchaseSuccess(remainingCoins: Int, remainingLives: Int)
    fun showInsufficientCoins(currentCoins: Int, requiredCoins: Int)
}