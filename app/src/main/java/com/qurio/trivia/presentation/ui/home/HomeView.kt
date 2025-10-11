package com.qurio.trivia.presentation.ui.home

import com.qurio.trivia.presentation.base.BaseView
import com.qurio.trivia.data.model.Category
import com.qurio.trivia.data.model.Difficulty
import com.qurio.trivia.data.model.GameResult
import com.qurio.trivia.data.model.UserProgress

interface HomeView : BaseView {
    fun displayUserProgress(userProgress: UserProgress)
    fun displayCategories(categories: List<Category>)
    fun displayLastGames(games: List<GameResult>)
    fun navigateToGame(categoryId: Int, categoryName: String, difficulty: Difficulty)
    fun showNotEnoughLives()
}