package com.qurio.trivia.ui.difficulty

import com.qurio.trivia.base.BasePresenter
import com.qurio.trivia.data.database.UserProgressDao
import com.qurio.trivia.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class DifficultyPresenter @Inject constructor(
    private val userProgressDao: UserProgressDao
) : BasePresenter<DifficultyView>() {

    fun startGame(categoryId: Int, difficulty: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val userProgress = userProgressDao.getUserProgress()

            if (userProgress != null && userProgress.lives > 0) {
                // Deduct one life
                userProgressDao.updateLives(userProgress.lives - 1)

                CoroutineScope(Dispatchers.Main).launch {
                    view?.navigateToGame()
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    view?.showNotEnoughLives()
                }
            }
        }
    }
}