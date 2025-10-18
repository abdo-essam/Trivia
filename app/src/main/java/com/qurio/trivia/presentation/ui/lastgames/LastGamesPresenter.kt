package com.qurio.trivia.presentation.ui.lastgames

import com.qurio.trivia.domain.repository.GameResultRepository
import com.qurio.trivia.presentation.base.BasePresenter
import javax.inject.Inject

class LastGamesPresenter @Inject constructor(
    private val gameResultRepository: GameResultRepository
) : BasePresenter<LastGamesView>() {

    fun loadAllGames() {
        tryToExecute(
            execute = {
                gameResultRepository.getAllGames()
            },
            onSuccess = { games ->
                withView { displayLastGames(games) }
            },
            onError = {
                withView { showError("Failed to load game history") }
            },
            showLoading = true
        )
    }
}