package com.qurio.trivia.presentation.ui.home

import android.util.Log
import com.qurio.trivia.presentation.base.BasePresenter
import com.qurio.trivia.data.database.DatabaseSeeder
import com.qurio.trivia.data.model.Category
import com.qurio.trivia.data.model.Difficulty
import com.qurio.trivia.data.repository.HomeRepository
import com.qurio.trivia.utils.Constants
import javax.inject.Inject

class HomePresenter @Inject constructor(
    private val repository: HomeRepository,
    private val databaseSeeder: DatabaseSeeder
) : BasePresenter<HomeView>() {

    // ========== Initialization ==========

    fun initializeData() {
        tryToExecute(
            execute = {
                databaseSeeder.seedDatabase()
            },
            onSuccess = {
                Log.d(TAG, "Database seeded successfully")
            },
            onError = { error ->
                Log.e(TAG, "Error seeding database", error)
            },
            showLoading = false
        )
    }

    // ========== Load User Progress ==========

    fun loadUserProgress() {
        tryToExecute(
            execute = {
                repository.getUserProgress()
            },
            onSuccess = { userProgress ->
                userProgress?.let {
                    withView { displayUserProgress(it) }
                } ?: withView {
                    showError("User progress not found")
                }
            },
            onError = { error ->
                Log.e(TAG, "Error loading user progress", error)
                withView { showError("Failed to load user data") }
            },
            showLoading = false
        )
    }

    // ========== Load Categories ==========

    fun loadCategories() {
        tryToExecute(
            execute = {
                repository.getCategories()
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

    // ========== Load Last Games ==========

    fun loadLastGames(limit: Int = DEFAULT_GAMES_LIMIT) {
        tryToExecute(
            execute = {
                repository.getLastGames(limit)
            },
            onSuccess = { games ->
                Log.d(TAG, "Loaded ${games.size} games")
                withView { displayLastGames(games) }
            },
            onError = { error ->
                Log.e(TAG, "Error loading last games", error)
                withView { showError("Failed to load game history") }
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
        userProgress: com.qurio.trivia.data.model.UserProgress?,
        category: Category,
        difficulty: Difficulty
    ) {
        when {
            userProgress == null -> {
                withView { showError("User progress not found") }
            }
            userProgress.lives > 0 -> {
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
                repository.updateLives(currentLives - 1)
                GameStartData(category.id, category.displayName, difficulty)
            },
            onSuccess = { gameData ->
                withView {
                    navigateToGame(gameData.categoryId, gameData.categoryName, gameData.difficulty)
                }
            },
            onError = { error ->
                Log.e(TAG, "Error starting game", error)
                withView { showError("Failed to start game") }
            },
            showLoading = false
        )
    }

    // ========== Purchase Life ==========

    fun purchaseLife() {
        tryToExecute(
            execute = {
                val userProgress = repository.getUserProgress()
                    ?: throw IllegalStateException("User progress not found")

                repository.purchaseLife(
                    currentCoins = userProgress.totalCoins,
                    currentLives = userProgress.lives,
                    lifeCost = LIFE_COST,
                    maxLives = Constants.MAX_LIVES
                )
            },
            onSuccess = { result ->
                handlePurchaseResult(result)
            },
            onError = { error ->
                Log.e(TAG, "Error purchasing life", error)
                withView { showError("Failed to purchase life") }
            },
            showLoading = true
        )
    }

    private fun handlePurchaseResult(result: HomeRepository.PurchaseResult) {
        when (result) {
            HomeRepository.PurchaseResult.Success -> {
                withView { showError("Life purchased successfully!") }
                loadUserProgress()
            }
            HomeRepository.PurchaseResult.NotEnoughCoins -> {
                withView { showError("Not enough coins to purchase life") }
            }
            HomeRepository.PurchaseResult.MaxLivesReached -> {
                withView { showError("You already have maximum lives!") }
            }
        }
    }

    // ========== Data Classes ==========

    private data class GameStartData(
        val categoryId: Int,
        val categoryName: String,
        val difficulty: Difficulty
    )

    companion object {
        private const val TAG = "HomePresenter"
        private const val LIFE_COST = 200
        private const val DEFAULT_GAMES_LIMIT = 3
    }
}