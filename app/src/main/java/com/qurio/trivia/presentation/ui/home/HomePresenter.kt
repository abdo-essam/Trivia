package com.qurio.trivia.presentation.ui.home

import android.util.Log
import com.qurio.trivia.base.BasePresenter
import com.qurio.trivia.data.database.DatabaseSeeder
import com.qurio.trivia.data.database.GameResultDao
import com.qurio.trivia.data.database.UserProgressDao
import com.qurio.trivia.data.model.Category
import com.qurio.trivia.data.model.Difficulty
import com.qurio.trivia.data.provider.DataProvider
import com.qurio.trivia.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HomePresenter @Inject constructor(
    private val userProgressDao: UserProgressDao,
    private val gameResultDao: GameResultDao,
    private val databaseSeeder: DatabaseSeeder
) : BasePresenter<HomeView>() {

    fun initializeData() {
        databaseSeeder.seedDatabase()
    }

    fun loadUserProgress() {
        CoroutineScope(Dispatchers.IO).launch {
            userProgressDao.getUserProgress()?.let { userProgress ->
                withContext(Dispatchers.Main) {
                    view?.displayUserProgress(userProgress)
                }
            }
        }
    }

    fun loadCategories() {
        CoroutineScope(Dispatchers.Main).launch {
            val categories = DataProvider.getCategories()
            Log.d(TAG, "Loading categories: ${categories.size}")
            view?.displayCategories(categories)
        }
    }

    fun loadLastGames(limit: Int = 3) {
        CoroutineScope(Dispatchers.IO).launch {
            val games = gameResultDao.getLastGames(limit)
            withContext(Dispatchers.Main) {
                Log.d(TAG, "Loading last games: ${games.size}")
                view?.displayLastGames(games)
            }
        }
    }

    fun checkLivesAndStartGame(category: Category?, difficulty: Difficulty) {
        category ?: return

        CoroutineScope(Dispatchers.IO).launch {
            val userProgress = userProgressDao.getUserProgress()

            withContext(Dispatchers.Main) {
                when {
                    userProgress == null -> {
                        view?.showError("User progress not found")
                    }
                    userProgress.lives > 0 -> {
                        deductLifeAndStartGame(userProgress.lives, category, difficulty)
                    }
                    else -> {
                        view?.showNotEnoughLives()
                    }
                }
            }
        }
    }

    private fun deductLifeAndStartGame(currentLives: Int, category: Category, difficulty: Difficulty) {
        CoroutineScope(Dispatchers.IO).launch {
            userProgressDao.updateLives(currentLives - 1)

            withContext(Dispatchers.Main) {
                view?.navigateToGame(category.id, category.displayName, difficulty)
            }
        }
    }

    fun purchaseLife() {
        CoroutineScope(Dispatchers.IO).launch {
            val userProgress = userProgressDao.getUserProgress() ?: return@launch

            val result = when {
                userProgress.totalCoins < LIFE_COST -> {
                    PurchaseResult.NotEnoughCoins
                }
                userProgress.lives >= Constants.MAX_LIVES -> {
                    PurchaseResult.MaxLivesReached
                }
                else -> {
                    userProgressDao.updateCoins(userProgress.totalCoins - LIFE_COST)
                    userProgressDao.updateLives(userProgress.lives + 1)
                    PurchaseResult.Success
                }
            }

            withContext(Dispatchers.Main) {
                when (result) {
                    PurchaseResult.Success -> {
                        view?.showError("Life purchased!")
                        loadUserProgress()
                    }
                    PurchaseResult.NotEnoughCoins -> {
                        view?.showError("Not enough coins!")
                    }
                    PurchaseResult.MaxLivesReached -> {
                        view?.showError("Maximum lives reached!")
                    }
                }
            }
        }
    }

    private sealed class PurchaseResult {
        object Success : PurchaseResult()
        object NotEnoughCoins : PurchaseResult()
        object MaxLivesReached : PurchaseResult()
    }

    companion object {
        private const val TAG = "HomePresenter"
        private const val LIFE_COST = 200
    }
}