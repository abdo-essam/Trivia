package com.qurio.trivia.presentation.ui.games

import android.util.Log
import com.qurio.trivia.presentation.base.BasePresenter
import com.qurio.trivia.domain.model.Category
import com.qurio.trivia.domain.model.Difficulty
import com.qurio.trivia.data.repository.GamesRepository
import com.qurio.trivia.domain.model.UserProgress
import javax.inject.Inject

class GamesPresenter @Inject constructor(
    private val repository: GamesRepository
) : BasePresenter<GamesView>() {

    // ========== Load Categories ==========

    fun loadAllCategories() {
        tryToExecute(
            execute = {
                repository.getAllCategories()
            },
            onSuccess = { categories ->
                Log.d(TAG, "Loaded ${categories.size} categories")
                withView { displayCategories(categories) }
            },
            onError = { error ->
                Log.e(TAG, "Error loading categories", error)
                withView { showError("Failed to load categories") }
            },
            showLoading = false
        )
    }

    // ========== Check Lives and Start Game ==========

    fun checkLivesAndStartGame(category: Category?, difficulty: Difficulty) {
        if (category == null) {
            withView { showError("Please select a category") }
            return
        }

        tryToExecute(
            execute = {
                repository.getUserProgress()
            },
            onSuccess = { userProgress ->
                handleGameStart(userProgress, category, difficulty)
            },
            onError = { error ->
                Log.e(TAG, "Error checking lives", error)
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
                withView { showError("User progress not found") }
            }
            userProgress.lives > 0 -> {
                deductLifeAndStartGame(category, difficulty)
            }
            else -> {
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
                val remainingLives = repository.deductLife()
                GameStartData(
                    categoryId = category.id,
                    categoryName = category.displayName,
                    difficulty = difficulty,
                    remainingLives = remainingLives
                )
            },
            onSuccess = { gameData ->
                Log.d(TAG, "Starting game. Remaining lives: ${gameData.remainingLives}")
                withView {
                    navigateToGame(
                        gameData.categoryId,
                        gameData.categoryName,
                        gameData.difficulty
                    )
                }
            },
            onError = { error ->
                Log.e(TAG, "Error starting game", error)
                withView { showError("Failed to start game") }
            },
            showLoading = false
        )
    }

    // ========== Data Classes ==========

    private data class GameStartData(
        val categoryId: Int,
        val categoryName: String,
        val difficulty: Difficulty,
        val remainingLives: Int
    )

    companion object {
        private const val TAG = "GamesPresenter"
    }
}