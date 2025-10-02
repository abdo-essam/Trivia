package com.qurio.trivia.ui.home

import com.qurio.trivia.base.BasePresenter
import com.qurio.trivia.data.database.UserProgressDao
import com.qurio.trivia.data.provider.DataProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomePresenter @Inject constructor(
    private val userProgressDao: UserProgressDao
) : BasePresenter<HomeView>() {

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

    fun loadCategories() {
        val categories = DataProvider.getCategories()
        view?.displayCategories(categories)
    }
}