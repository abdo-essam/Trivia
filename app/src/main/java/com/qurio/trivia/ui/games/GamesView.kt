package com.qurio.trivia.ui.games

import com.qurio.trivia.base.BaseView
import com.qurio.trivia.data.model.Category
import com.qurio.trivia.data.model.Difficulty

interface GamesView : BaseView {
    fun displayCategories(categories: List<Category>)
    fun navigateToGame(categoryId: Int, categoryName: String, difficulty: Difficulty)
    fun showNotEnoughLives()
}