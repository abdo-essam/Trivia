package com.qurio.trivia.presentation.ui.home

import android.util.Log
import com.qurio.trivia.data.database.DatabaseSeeder
import com.qurio.trivia.domain.model.Category
import com.qurio.trivia.domain.model.Difficulty
import com.qurio.trivia.domain.repository.HomeRepository
import com.qurio.trivia.presentation.base.BasePresenter
import javax.inject.Inject

/**
 * Presenter for Home screen
 * Handles loading user data, categories, and game management
 */
class HomePresenter @Inject constructor(
    private val homeRepository: HomeRepository,
    private val databaseSeeder: DatabaseSeeder
) : BasePresenter<HomeView>() {
    companion object {
        private const val TAG = "HomePresenter"
        private const val DEFAULT_GAMES_LIMIT = 3
    }

    fun initializeData() {
        tryToExecute(
            execute = {
                databaseSeeder.seedDatabase()
            },
            onSuccess = {
                Log.d(TAG, "✓ Database initialized successfully")
                checkStreak() // Check streak after initialization
            },
            onError = { error ->
                Log.e(TAG, "✗ Database initialization failed", error)
            },
            showLoading = false
        )
    }

    private fun checkStreak() {
        tryToExecute(
            execute = {
                homeRepository.checkAndUpdateStreak()
            },
            onSuccess = { userProgress ->
                userProgress?.let {
                    Log.d(TAG, "✓ Streak checked: ${it.currentStreak} days")
                }
            },
            onError = { error ->
                Log.e(TAG, "✗ Failed to check streak", error)
            },
            showLoading = false
        )
    }

    fun loadUserProgress() {
        tryToExecute(
            execute = {
                homeRepository.getUserProgress()
            },
            onSuccess = { userProgress ->
                if (userProgress != null) {
                    Log.d(TAG, "✓ User progress loaded: ${userProgress.lives} lives, ${userProgress.totalCoins} coins, ${userProgress.currentStreak} day streak")
                    withView { displayUserProgress(userProgress) }
                } else {
                    Log.w(TAG, "✗ User progress not found")
                    withView { showError("User data not found") }
                }
            },
            onError = { error ->
                Log.e(TAG, "✗ Failed to load user progress", error)
                withView { showError("Failed to load user data") }
            },
            showLoading = false
        )
    }

    fun loadCategories() {
        tryToExecute(
            execute = {
                homeRepository.getCategories()
            },
            onSuccess = { categories ->
                Log.d(TAG, "✓ Loaded ${categories.size} categories")
                withView { displayCategories(categories) }
            },
            onError = { error ->
                Log.e(TAG, "✗ Failed to load categories", error)
                withView { showError("Failed to load categories") }
            },
            showLoading = false
        )
    }

    fun loadLastGames(limit: Int = DEFAULT_GAMES_LIMIT) {
        tryToExecute(
            execute = {
                homeRepository.getLastGames(limit)
            },
            onSuccess = { games ->
                Log.d(TAG, "✓ Loaded ${games.size} recent games")
                withView { displayLastGames(games) }
            },
            onError = { error ->
                Log.e(TAG, "✗ Failed to load game history", error)
                withView { showError("Failed to load game history") }
            },
            showLoading = false
        )
    }

    // ========== Game Start Logic ==========

    fun checkLivesAndStartGame(category: Category?, difficulty: Difficulty) {
        if (category == null) {
            Log.w(TAG, "✗ Cannot start game: category is null")
            withView { showError("Please select a category") }
            return
        }

        Log.d(TAG, "Checking lives for game: ${category.displayName} (${difficulty.displayName})")

        tryToExecute(
            execute = {
                homeRepository.getUserProgress()
            },
            onSuccess = { userProgress ->
                handleGameStart(userProgress, category, difficulty)
            },
            onError = { error ->
                Log.e(TAG, "✗ Failed to check lives", error)
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
                Log.e(TAG, "✗ User progress is null")
                withView { showError("User data not found") }
            }
            userProgress.hasEnoughLives() -> {
                Log.d(TAG, "✓ User has ${userProgress.lives} lives, starting game")
                deductLifeAndStartGame(userProgress.lives, category, difficulty)
            }
            else -> {
                Log.w(TAG, "✗ Not enough lives: ${userProgress.lives}")
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
                Log.d(TAG, "✓ Life deducted: $currentLives -> $newLives")
                Triple(category.id, category.displayName, difficulty)
            },
            onSuccess = { (categoryId, categoryName, diff) ->
                Log.d(TAG, "✓ Navigating to game: $categoryName")
                withView {
                    navigateToGame(categoryId, categoryName, diff)
                }
            },
            onError = { error ->
                Log.e(TAG, "✗ Failed to start game", error)
                withView { showError("Failed to start game") }
            },
            showLoading = false
        )
    }
}