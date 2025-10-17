package com.qurio.trivia.presentation.ui.games.managers

import com.qurio.trivia.R
import com.qurio.trivia.databinding.FragmentGamesBinding
import com.qurio.trivia.databinding.TopBarBinding
import com.qurio.trivia.domain.model.Category
import com.qurio.trivia.presentation.ui.games.adapter.AllGamesAdapter

class GamesUIManager(
    private val binding: FragmentGamesBinding,
    private val adapter: AllGamesAdapter
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

    fun displayCategories(categories: List<Category>) {
        adapter.submitList(categories)
    }
}