package com.qurio.trivia.presentation.ui.games

import com.qurio.trivia.presentation.base.BaseView
import com.qurio.trivia.data.model.Category
import com.qurio.trivia.data.model.Difficulty

interface GamesView : BaseView {
    fun displayCategories(categories: List<Category>)
    fun navigateToGame(categoryId: Int, categoryName: String, difficulty: Difficulty)
    fun showNotEnoughLives()
}