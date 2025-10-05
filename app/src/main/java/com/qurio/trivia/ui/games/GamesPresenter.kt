package com.qurio.trivia.ui.games

import com.qurio.trivia.base.BasePresenter
import com.qurio.trivia.data.provider.DataProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class GamesPresenter @Inject constructor() : BasePresenter<GamesView>() {

    fun loadAllCategories() {
        CoroutineScope(Dispatchers.Main).launch {
            val categories = DataProvider.getCategories()
            view?.displayCategories(categories)
        }
    }
}