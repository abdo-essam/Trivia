package com.qurio.trivia.presentation.ui.lastgames

import android.util.Log
import com.qurio.trivia.domain.model.GameResult
import com.qurio.trivia.domain.repository.GameHistoryRepository
import com.qurio.trivia.presentation.base.BasePresenter
import javax.inject.Inject

/**
 * Presenter for LastGames screen
 * Handles loading and displaying game history
 */
class LastGamesPresenter @Inject constructor(
    private val gameHistoryRepository: GameHistoryRepository
) : BasePresenter<LastGamesView>() {

    companion object {
        private const val TAG = "LastGamesPresenter"
    }

    /**
     * Load all game results
     */
    fun loadAllGames() {
        tryToExecute(
            execute = {
                gameHistoryRepository.getAllGames()
            },
            onSuccess = { games ->
                Log.d(TAG, "✓ Loaded ${games.size} games")
                handleGamesLoaded(games)
            },
            onError = { error ->
                Log.e(TAG, "✗ Failed to load games", error)
                withView { showError("Failed to load game history") }
            },
            showLoading = true
        )
    }

    private fun handleGamesLoaded(games: List<GameResult>) {
        withView {
            if (games.isEmpty()) {
                Log.d(TAG, "No games found, showing empty state")
                showEmptyState()
            } else {
                Log.d(TAG, "Displaying ${games.size} games")
                hideEmptyState()
                displayLastGames(games)
            }
        }
    }
}