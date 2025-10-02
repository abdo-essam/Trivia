package com.qurio.trivia.ui.buylife

import com.qurio.trivia.base.BaseView
import com.qurio.trivia.data.model.UserProgress

interface BuyLifeView : BaseView {
    fun displayUserProgress(userProgress: UserProgress)
    fun showPurchaseSuccess()
    fun showInsufficientCoins()
    fun showAdReward()
    fun navigateBack()
}