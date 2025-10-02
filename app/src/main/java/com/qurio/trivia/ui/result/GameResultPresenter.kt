package com.qurio.trivia.ui.result

import com.qurio.trivia.base.BasePresenter
import javax.inject.Inject

class GameResultPresenter @Inject constructor() : BasePresenter<GameResultView>() {

    fun playAgain() {
        view?.navigateToPlayAgain()
    }

    fun backToHome() {
        view?.navigateToHome()
    }
}