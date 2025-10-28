package com.qurio.trivia.presentation.ui.lastgames.managers

import com.qurio.trivia.R
import com.qurio.trivia.databinding.FragmentLastGamesBinding
import com.qurio.trivia.databinding.TopBarBinding
import com.qurio.trivia.domain.model.GameResult
import com.qurio.trivia.presentation.ui.lastgames.adapters.LastGamesAdapter

class LastGamesUIManager(
    private val binding: FragmentLastGamesBinding,
    private val adapter: LastGamesAdapter
) {
    private val topBarBinding: TopBarBinding by lazy {
        TopBarBinding.bind(binding.root.findViewById(R.id.top_bar))
    }

    fun setupTopBar(title: String, onBackClick: () -> Unit) {
        topBarBinding.apply {
            tvTitle.text = title
            btnBack.setOnClickListener { onBackClick() }
        }
    }

    fun displayGames(games: List<GameResult>) {
        adapter.submitList(games)
    }
}