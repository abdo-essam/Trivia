package com.qurio.trivia.presentation.ui.games

import com.qurio.trivia.domain.model.Category
import com.qurio.trivia.domain.model.Difficulty
import com.qurio.trivia.presentation.base.BaseView

/**
 * View contract for Games screen
 */
interface GamesView : BaseView {
    /**
     * Display list of available categories
     */
    fun displayCategories(categories: List<Category>)

    /**
     * Navigate to game screen with selected category and difficulty
     */
    fun navigateToGame(categoryId: Int, categoryName: String, difficulty: Difficulty)

    /**
     * Show dialog when user doesn't have enough lives
     */
    fun showNotEnoughLives()
}