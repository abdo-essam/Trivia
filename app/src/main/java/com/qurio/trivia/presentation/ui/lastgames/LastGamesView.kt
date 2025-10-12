package com.qurio.trivia.presentation.ui.lastgames

import com.qurio.trivia.domain.model.GameResult
import com.qurio.trivia.presentation.base.BaseView

/**
 * View contract for LastGames screen
 */
interface LastGamesView : BaseView {
    /**
     * Display list of game results
     */
    fun displayLastGames(games: List<GameResult>)

    /**
     * Show empty state when no games found
     */
    fun showEmptyState()

    /**
     * Hide empty state
     */
    fun hideEmptyState()
}