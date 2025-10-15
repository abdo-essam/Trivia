package com.qurio.trivia.presentation.ui.result

import com.qurio.trivia.presentation.base.BaseView

interface GameResultView : BaseView {
    fun navigateToHome()
    fun navigateToPlayAgain()
}