package com.qurio.trivia.presentation.ui.lastgames

import android.util.Log
import com.qurio.trivia.presentation.base.BasePresenter
import com.qurio.trivia.data.repository.LastGamesRepository
import javax.inject.Inject

class LastGamesPresenter @Inject constructor(
    private val repository: LastGamesRepository
) : BasePresenter<LastGamesView>() {

    // ========== Load All Games ==========

    fun loadAllLastGames() {
        tryToExecute(
            execute = {
                repository.getAllGames()
            },
            onSuccess = { games ->
                Log.d(TAG, "Loaded ${games.size} games")
                withView { displayLastGames(games) }
            },
            onError = { error ->
                Log.e(TAG, "Error loading games", error)
                withView { showError("Failed to load game history") }
            },
            showLoading = true
        )
    }

    companion object {
        private const val TAG = "LastGamesPresenter"
    }
}