package com.qurio.trivia.presentation.ui.lastgames

import android.util.Log
import com.qurio.trivia.base.BasePresenter
import com.qurio.trivia.data.database.GameResultDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LastGamesPresenter @Inject constructor(
    private val gameResultDao: GameResultDao
) : BasePresenter<LastGamesView>() {

    fun loadAllLastGames() {
        CoroutineScope(Dispatchers.IO).launch {
            val games = gameResultDao.getAllGames()
            withContext(Dispatchers.Main) {
                Log.d("LastGamesPresenter", "Loaded ${games.size} games")
                view?.displayLastGames(games)
            }
        }
    }
}