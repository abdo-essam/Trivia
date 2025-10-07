package com.qurio.trivia.ui.home

import com.qurio.trivia.base.BaseView
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