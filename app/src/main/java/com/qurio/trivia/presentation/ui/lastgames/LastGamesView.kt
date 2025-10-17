package com.qurio.trivia.presentation.ui.lastgames

import com.qurio.trivia.domain.model.GameResult
import com.qurio.trivia.presentation.base.BaseView

interface LastGamesView : BaseView {
    fun displayLastGames(games: List<GameResult>)
}