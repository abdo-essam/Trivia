package com.qurio.trivia.presentation.ui.games

import android.util.Log
import com.qurio.trivia.domain.model.Category
import com.qurio.trivia.domain.model.Difficulty
import com.qurio.trivia.domain.model.UserProgress
import com.qurio.trivia.domain.repository.GamesRepository
import com.qurio.trivia.presentation.base.BasePresenter
import javax.inject.Inject

/**
 * Presenter for Games screen
 * Handles category loading and game start logic
 */
class GamesPresenter @Inject constructor(
    private val gamesRepository: GamesRepository
) : BasePresenter<GamesView>() {

    companion object {
        private const val TAG = "GamesPresenter"
    }

    // ========== Load Categories ==========

    /**
     * Load all available game categories
     */
    fun loadAllCategories() {
        tryToExecute(
            execute = {
                gamesRepository.getAllCategories()
            },
            onSuccess = { categories ->
                Log.d(TAG, "✓ Loaded ${categories.size} categories")
                withView { displayCategories(categories) }
            },
            onError = { error ->
                Log.e(TAG, "✗ Failed to load categories", error)
                withView { showError("Failed to load categories") }
            },
            showLoading = true
        )
    }

    // ========== Game Start Logic ==========

    /**
     * Check if user has enough lives and start game
     */
    fun checkLivesAndStartGame(category: Category?, difficulty: Difficulty) {
        if (category == null) {
            Log.w(TAG, "✗ Cannot start game: category is null")
            withView { showError("Please select a category") }
            return
        }

        Log.d(TAG, "Checking lives for game: ${category.displayName} (${difficulty.displayName})")

        tryToExecute(
            execute = {
                gamesRepository.getUserProgress()
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
        userProgress: UserProgress?,
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
                deductLifeAndStartGame(category, difficulty)
            }
            else -> {
                Log.w(TAG, "✗ Not enough lives: ${userProgress.lives}")
                withView { showNotEnoughLives() }
            }
        }
    }

    private fun deductLifeAndStartGame(
        category: Category,
        difficulty: Difficulty
    ) {
        tryToExecute(
            execute = {
                val newLives = gamesRepository.deductLife()
                Log.d(TAG, "✓ Life deducted, remaining: $newLives")
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