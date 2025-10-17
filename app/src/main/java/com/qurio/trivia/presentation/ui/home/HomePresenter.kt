package com.qurio.trivia.presentation.ui.home

import com.qurio.trivia.domain.model.Category
import com.qurio.trivia.domain.model.Difficulty
import com.qurio.trivia.domain.repository.AchievementsRepository
import com.qurio.trivia.domain.repository.HomeRepository
import com.qurio.trivia.presentation.base.BasePresenter
import javax.inject.Inject

class HomePresenter @Inject constructor(
    private val homeRepository: HomeRepository,
    private val achievementsRepository: AchievementsRepository
) : BasePresenter<HomeView>() {

    companion object {
        private const val DEFAULT_GAMES_LIMIT = 3
    }

    // ========== Load Data ==========

    fun loadUserProgress() {
        tryToExecute(
            execute = {
                homeRepository.getUserProgress()
            },
            onSuccess = { userProgress ->
                if (userProgress != null) {
                    withView { displayUserProgress(userProgress) }
                } else {
                    withView { showError("User data not found") }
                }
            },
            onError = { error ->
                withView { showError("Failed to load user data") }
            },
            showLoading = true
        )
    }

    fun loadUnlockedAchievements() {
        tryToExecute(
            execute = {
                achievementsRepository.getUnlockedCount()
            },
            onSuccess = { count ->
                withView { displayUnlockedAchievements(count) }
            },
            onError = { error ->
                // Silently fail - not critical
            },
            showLoading = false
        )
    }

    fun loadCategories() {
        tryToExecute(
            execute = {
                val categories = homeRepository.getCategories()
                categories
            },
            onSuccess = { categories ->
                withView {
                    displayCategories(categories)
                }
            },
            onError = { error ->
                error.printStackTrace()
                withView { showError("Failed to load categories") }
            },
            showLoading = true
        )
    }

    fun loadLastGames(limit: Int = DEFAULT_GAMES_LIMIT) {
        tryToExecute(
            execute = {
                homeRepository.getLastGames(limit)
            },
            onSuccess = { games ->
                withView { displayLastGames(games) }
            },
            onError = { error ->
                withView { showError("Failed to load game history") }
            },
            showLoading = false
        )
    }

    fun checkAndUpdateStreak() {
        tryToExecute(
            execute = {
                homeRepository.checkAndUpdateStreak()
            },
            onSuccess = { userProgress ->
                userProgress?.let {
                }
            },
            onError = { error ->
            },
            showLoading = false
        )
    }

    // ========== Game Start Logic ==========

    fun checkLivesAndStartGame(category: Category?, difficulty: Difficulty) {
        if (category == null) {
            withView { showError("Please select a category") }
            return
        }

        tryToExecute(
            execute = {
                homeRepository.getUserProgress()
            },
            onSuccess = { userProgress ->
                handleGameStart(userProgress, category, difficulty)
            },
            onError = { error ->
                withView { showError("Failed to start game") }
            },
            showLoading = true
        )
    }

    private fun handleGameStart(
        userProgress: com.qurio.trivia.domain.model.UserProgress?,
        category: Category,
        difficulty: Difficulty
    ) {
        when {
            userProgress == null -> {
                withView { showError("User data not found") }
            }
            userProgress.hasEnoughLives() -> {
                deductLifeAndStartGame(userProgress.lives, category, difficulty)
            }
            else -> {
                withView { showNotEnoughLives() }
            }
        }
    }

    private fun deductLifeAndStartGame(
        currentLives: Int,
        category: Category,
        difficulty: Difficulty
    ) {
        tryToExecute(
            execute = {
                val newLives = currentLives - 1
                homeRepository.updateLives(newLives)
                Triple(category.id, category.displayName, difficulty)
            },
            onSuccess = { (categoryId, categoryName, diff) ->
                withView {
                    navigateToGame(categoryId, categoryName, diff)
                }
            },
            onError = { error ->
                withView { showError("Failed to start game") }
            },
            showLoading = false
        )
    }
}