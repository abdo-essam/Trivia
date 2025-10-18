package com.qurio.trivia.presentation.ui.games

import com.qurio.trivia.domain.model.Category
import com.qurio.trivia.domain.model.Difficulty
import com.qurio.trivia.domain.model.UserProgress
import com.qurio.trivia.domain.repository.CategoryRepository
import com.qurio.trivia.domain.repository.UserRepository
import com.qurio.trivia.presentation.base.BasePresenter
import javax.inject.Inject

class GamesPresenter @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val userRepository: UserRepository
) : BasePresenter<GamesView>() {

    fun loadAllCategories() {
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
            showLoading = true
        )
    }

    fun checkLivesAndStartGame(category: Category, difficulty: Difficulty) {
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
        userProgress: UserProgress?,
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