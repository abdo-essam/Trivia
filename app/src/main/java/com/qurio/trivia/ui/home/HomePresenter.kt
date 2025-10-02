package com.qurio.trivia.ui.home

import android.util.Log
import com.qurio.trivia.base.BasePresenter
import com.qurio.trivia.data.database.DatabaseSeeder
import com.qurio.trivia.data.database.GameResultDao
import com.qurio.trivia.data.database.UserProgressDao
import com.qurio.trivia.data.provider.DataProvider
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
        // Seed database with fake data for testing
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
}