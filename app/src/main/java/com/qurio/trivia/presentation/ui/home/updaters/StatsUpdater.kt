package com.qurio.trivia.presentation.ui.home.updaters

import androidx.core.view.isVisible
import com.qurio.trivia.databinding.ItemStatsBinding
import com.qurio.trivia.domain.model.UserProgress
import com.qurio.trivia.utils.extensions.formatWithCommas

class StatsUpdater(
    private val binding: ItemStatsBinding
) {
    fun update(userProgress: UserProgress) {
        binding.apply {
            tvLives.text = userProgress.lives.toString()
            tvCoins.text = userProgress.totalCoins.formatWithCommas()
            ivCrown.isVisible = userProgress.totalCoins >= CROWN_THRESHOLD
        }
    }

    fun updateAchievements(unlockedCount: Int) {
        binding.tvAwards.text = unlockedCount.toString()
    }

    fun setOnLivesClickListener(onClick: () -> Unit) {
        binding.statsLivesContainer.setOnClickListener { onClick() }
    }

    fun setOnAwardsClickListener(onClick: () -> Unit) {
        binding.statsAwardsContainer.setOnClickListener { onClick() }
    }

    companion object {
        private const val CROWN_THRESHOLD = 10_000
    }

}