package com.qurio.trivia.ui.lastgames

import com.qurio.trivia.base.BaseView
import com.qurio.trivia.data.model.GameResult

interface LastGamesView : BaseView {
    fun displayLastGames(games: List<GameResult>)
}