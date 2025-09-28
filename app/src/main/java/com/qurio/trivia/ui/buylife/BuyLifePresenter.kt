package com.qurio.trivia.ui.buylife

import com.qurio.trivia.base.BasePresenter
import com.qurio.trivia.data.database.UserProgressDao
import com.qurio.trivia.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class BuyLifePresenter @Inject constructor(
    private val userProgressDao: UserProgressDao
) : BasePresenter<BuyLifeView>() {

    fun loadUserProgress() {
        CoroutineScope(Dispatchers.IO).launch {
            val userProgress = userProgressDao.getUserProgress()
            userProgress?.let {
                CoroutineScope(Dispatchers.Main).launch {
                    view?.displayUserProgress(it)
                }
            }
        }
    }

    fun buyLife(cost: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val userProgress = userProgressDao.getUserProgress()
            userProgress?.let {
                if (it.totalCoins >= cost && it.lives < Constants.MAX_LIVES) {
// Deduct coins and add life
                    userProgressDao.updateCoins(it.totalCoins - cost)
                    userProgressDao.updateLives(it.lives + 1)
                    CoroutineScope(Dispatchers.Main).launch {
                        view?.showPurchaseSuccess()
                    }
                } else if (it.totalCoins < cost) {
                    CoroutineScope(Dispatchers.Main).launch {
                        view?.showInsufficientCoins()
                    }
                }
            }
        }
    }

    fun watchAdForLife() {
        // Simulate watching an ad
        CoroutineScope(Dispatchers.IO).launch {
            val userProgress = userProgressDao.getUserProgress()
            userProgress?.let {
                if (it.lives < Constants.MAX_LIVES) {
                    userProgressDao.updateLives(it.lives + 1)

                    CoroutineScope(Dispatchers.Main).launch {
                        view?.showAdReward()
                    }
                }
            }
        }
    }
}