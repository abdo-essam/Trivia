package com.qurio.trivia.ui.home

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
            val userProgress = userProgressDao.getUserProgress()
            userProgress?.let {
                withContext(Dispatchers.Main) {
                    view?.displayUserProgress(it)
                }
            }
        }
    }

    fun loadCategories() {
        CoroutineScope(Dispatchers.Main).launch {
            val categories = DataProvider.getCategories()
            Log.d("HomePresenter", "Loading categories: ${categories.size}")
            view?.displayCategories(categories)
        }
    }

    fun loadLastGames(limit: Int = 3) {
        CoroutineScope(Dispatchers.IO).launch {
            val games = gameResultDao.getLastGames(limit)
            withContext(Dispatchers.Main) {
                Log.d("HomePresenter", "Loading last games: ${games.size}")
                view?.displayLastGames(games)
            }
        }
    }

    fun checkLivesAndStartGame(category: Category?, difficulty: Difficulty) {
        if (category == null) return

        CoroutineScope(Dispatchers.IO).launch {
            val userProgress = userProgressDao.getUserProgress()

            withContext(Dispatchers.Main) {
                if (userProgress != null && userProgress.lives > 0) {
                    // Deduct one life
                    CoroutineScope(Dispatchers.IO).launch {
                        userProgressDao.updateLives(userProgress.lives - 1)
                    }

                    // Navigate to game
                    view?.navigateToGame(category.id, category.displayName, difficulty)
                } else {
                    // Not enough lives
                    view?.showNotEnoughLives()
                }
            }
        }
    }

    fun purchaseLife() {
        CoroutineScope(Dispatchers.IO).launch {
            val userProgress = userProgressDao.getUserProgress()
            userProgress?.let {
                val lifeCost = 200

                if (it.totalCoins >= lifeCost && it.lives < Constants.MAX_LIVES) {
                    userProgressDao.updateCoins(it.totalCoins - lifeCost)
                    userProgressDao.updateLives(it.lives + 1)

                    withContext(Dispatchers.Main) {
                        view?.showError("Life purchased!")
                        loadUserProgress() // Refresh UI
                    }
                } else if (it.totalCoins < lifeCost) {
                    withContext(Dispatchers.Main) {
                        view?.showError("Not enough coins!")
                    }
                }
            }
        }
    }
}