package com.qurio.trivia.ui.home

import android.util.Log
import com.qurio.trivia.base.BasePresenter
import com.qurio.trivia.data.database.UserProgressDao
import com.qurio.trivia.data.provider.DataProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HomePresenter @Inject constructor(
    private val userProgressDao: UserProgressDao
) : BasePresenter<HomeView>() {

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
}