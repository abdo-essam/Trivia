package com.qurio.trivia.ui.difficulty

import com.qurio.trivia.base.BaseView

interface DifficultyView : BaseView {
    fun navigateToGame()
    fun showNotEnoughLives()
}