package com.qurio.trivia.ui.games

import com.qurio.trivia.base.BasePresenter
import com.qurio.trivia.data.database.UserProgressDao
import com.qurio.trivia.data.model.Category
import com.qurio.trivia.data.provider.DataProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GamesPresenter @Inject constructor(
    private val userProgressDao: UserProgressDao
) : BasePresenter<GamesView>() {

    fun loadAllCategories() {
        CoroutineScope(Dispatchers.Main).launch {
            val categories = DataProvider.getCategories()
            view?.displayCategories(categories)
        }
    }

    fun checkLivesAndStartGame(category: Category?, difficulty: String) {
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
}