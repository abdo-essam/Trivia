package com.qurio.trivia.presentation.ui.home

import com.qurio.trivia.domain.model.Category
import com.qurio.trivia.domain.model.Difficulty
import com.qurio.trivia.domain.model.GameResult
import com.qurio.trivia.domain.model.UserProgress
import com.qurio.trivia.presentation.base.BaseView

interface HomeView : BaseView {
    fun displayUserProgress(userProgress: UserProgress)
    fun displayCategories(categories: List<Category>)
    fun displayLastGames(games: List<GameResult>)
    fun displayUnlockedAchievements(unlockedCount: Int)
    fun navigateToGame(categoryId: Int, categoryName: String, difficulty: Difficulty)
    fun showNotEnoughLives()
}