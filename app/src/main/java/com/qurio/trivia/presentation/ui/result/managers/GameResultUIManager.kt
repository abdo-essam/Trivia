package com.qurio.trivia.presentation.ui.result.managers

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.qurio.trivia.R
import com.qurio.trivia.databinding.FragmentGameResultBinding
import com.qurio.trivia.presentation.ui.result.model.GameResultStats

class GameResultUIManager(
    private val binding: FragmentGameResultBinding
) {

    fun displayResults(stats: GameResultStats) {
        toggleResultSections(stats.isWon)

        if (stats.isWon) {
            displayVictorySection(stats.stars)
        }

        displayReward(stats.coins)
        displayStatistics(stats)
        updateShareButton(stats.isWon)
    }

    private fun toggleResultSections(isWon: Boolean) {
        binding.victorySection.root.isVisible = isWon
        binding.loseSection.root.isVisible = !isWon
    }

    private fun displayVictorySection(stars: Int) {
        binding.victorySection.apply {
            ivStar1.isVisible = stars >= 1
            ivStar2.isVisible = stars >= 2
            ivStar3.isVisible = stars >= 3
        }
    }

    private fun displayReward(coins: Int) {
        binding.layoutResultContent.tvCoinsEarned.text = coins.toString()
    }

    private fun displayStatistics(stats: GameResultStats) {
        binding.layoutResultContent.layoutStatistics.apply {
            setupStatCard(
                card = cardCorrect,
                label = binding.root.context.getString(R.string.correct),
                value = stats.correct.toString()
            )

            setupStatCard(
                card = cardIncorrect,
                label = binding.root.context.getString(R.string.incorrect),
                value = stats.incorrect.toString()
            )

            setupStatCard(
                card = cardSkipped,
                label = binding.root.context.getString(R.string.skipped),
                value = stats.skipped.toString()
            )
        }
    }

    private fun setupStatCard(card: View, label: String, value: String) {
        card.findViewById<TextView>(R.id.tv_stat_label)?.text = label
        card.findViewById<TextView>(R.id.tv_stat_value)?.text = value
    }

    private fun updateShareButton(isWon: Boolean) {
        binding.btnShare.text = binding.root.context.getString(
            if (isWon) R.string.share_win_with_friends else R.string.share_disappointment
        )
    }
}