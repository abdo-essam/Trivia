package com.qurio.trivia.ui.result

import com.qurio.trivia.base.BaseView

interface GameResultView : BaseView {
    fun navigateToHome()
    fun navigateToPlayAgain()
}