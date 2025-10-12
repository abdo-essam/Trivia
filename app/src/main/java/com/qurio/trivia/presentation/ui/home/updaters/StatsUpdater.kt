package com.qurio.trivia.presentation.ui.home.updaters

import androidx.core.view.isVisible
import com.qurio.trivia.databinding.ItemStatsBinding
import com.qurio.trivia.domain.model.UserProgress
import com.qurio.trivia.utils.extensions.formatWithCommas

/**
 * Handles stats section UI updates
 */
class StatsUpdater(
    private val binding: ItemStatsBinding
) {
    companion object {
        private const val CROWN_THRESHOLD = 10_000
    }

    fun update(userProgress: UserProgress) {
        binding.apply {
            tvLives.text = userProgress.lives.toString()
            tvCoins.text = userProgress.totalCoins.formatWithCommas()
            tvAwards.text = userProgress.awards.toString()
            ivCrown.isVisible = userProgress.totalCoins >= CROWN_THRESHOLD
        }
    }

    fun setOnLivesClickListener(onClick: () -> Unit) {
        binding.statsLivesContainer.setOnClickListener { onClick() }
    }

    fun setOnAwardsClickListener(onClick: () -> Unit) {
        binding.statsAwardsContainer.setOnClickListener { onClick() }
    }
}