package com.qurio.trivia.presentation.ui.lastgames

import com.qurio.trivia.presentation.base.BaseView
import com.qurio.trivia.data.model.GameResult

interface LastGamesView : BaseView {
    fun displayLastGames(games: List<GameResult>)
}