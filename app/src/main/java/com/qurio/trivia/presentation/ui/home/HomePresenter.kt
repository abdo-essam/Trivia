package com.qurio.trivia.presentation.ui.home

import com.qurio.trivia.domain.model.Category
import com.qurio.trivia.domain.model.Difficulty
import com.qurio.trivia.domain.repository.AchievementsRepository
import com.qurio.trivia.domain.repository.CategoryRepository
import com.qurio.trivia.domain.repository.GameResultRepository
import com.qurio.trivia.domain.repository.UserRepository
import com.qurio.trivia.presentation.base.BasePresenter
import javax.inject.Inject

class HomePresenter @Inject constructor(
    private val userRepository: UserRepository,
    private val categoryRepository: CategoryRepository,
    private val gameResultRepository: GameResultRepository,
    private val achievementsRepository: AchievementsRepository
) : BasePresenter<HomeView>() {

    companion object {
        private const val DEFAULT_GAMES_LIMIT = 3
    }

    fun loadUserProgress() {
        tryToExecute(
            execute = {
                userRepository.getUserProgress()
            },
            onSuccess = { userProgress ->
                if (userProgress != null) {
                    withView { displayUserProgress(userProgress) }
                } else {
                    withView { showError("User data not found") }
                }
            },
            onError = {
                withView { showError("Failed to load user data") }
            },
            showLoading = false
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
            onError = {},
            showLoading = false
        )
    }

    fun loadCategories() {
        tryToExecute(
            execute = {
                categoryRepository.getAllCategories()
            },
            onSuccess = { categories ->
                withView { displayCategories(categories) }
            },
            onError = {
                withView { showError("Failed to load categories") }
            },
            showLoading = false
        )
    }

    fun loadLastGames(limit: Int = DEFAULT_GAMES_LIMIT) {
        tryToExecute(
            execute = {
                gameResultRepository.getLastGames(limit)
            },
            onSuccess = { games ->
                withView { displayLastGames(games) }
            },
            onError = {
                withView { showError("Failed to load game history") }
            },
            showLoading = false
        )
    }

    fun checkAndUpdateStreak() {
        tryToExecute(
            execute = {
                userRepository.checkAndUpdateStreak()
            },
            onSuccess = {},
            onError = {},
            showLoading = false
        )
    }

    fun checkLivesAndStartGame(category: Category?, difficulty: Difficulty) {
        if (category == null) {
            withView { showError("Please select a category") }
            return
        }

        tryToExecute(
            execute = {
                userRepository.getUserProgress()
            },
            onSuccess = { userProgress ->
                handleGameStart(userProgress, category, difficulty)
            },
            onError = {
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
                userRepository.deductLife()
                Triple(category.id, category.displayName, difficulty)
            },
            onSuccess = { (categoryId, categoryName, diff) ->
                withView {
                    navigateToGame(categoryId, categoryName, diff)
                }
            },
            onError = {
                withView { showError("Failed to start game") }
            },
            showLoading = false
        )
    }
}