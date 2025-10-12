package com.qurio.trivia.presentation.ui.result

import com.qurio.trivia.presentation.base.BaseView

/**
 * View contract for GameResultFragment
 */
interface GameResultView : BaseView {
    fun navigateToHome()
    fun navigateToPlayAgain()
    fun onGameResultSaved(coins: Int, stars: Int)
}