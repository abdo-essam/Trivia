package com.qurio.trivia.presentation.ui.home

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.qurio.trivia.R
import com.qurio.trivia.databinding.FragmentHomeBinding
import com.qurio.trivia.databinding.ItemStatsBinding
import com.qurio.trivia.databinding.ItemStreakBinding
import com.qurio.trivia.databinding.SectionHeaderBinding
import com.qurio.trivia.databinding.TopBarHomeBinding
import com.qurio.trivia.domain.model.UserProgress
import com.qurio.trivia.utils.extensions.capitalizeFirst
import com.qurio.trivia.utils.extensions.loadCharacterImage

/**
 * Helper class to update Home UI
 * Extracts UI update logic from Fragment
 */
class HomeUIUpdater(
    private val binding: FragmentHomeBinding,
    private val context: Context
) {
    val topBar: TopBarHomeBinding by lazy {
        TopBarHomeBinding.bind(binding.root.findViewById(R.id.top_bar_home))
    }

    val stats: ItemStatsBinding by lazy {
        ItemStatsBinding.bind(binding.root.findViewById(R.id.layout_stats))
    }

    private val streak: ItemStreakBinding by lazy {
        ItemStreakBinding.bind(binding.root.findViewById(R.id.layout_streak))
    }

    private val sectionHeaderGames: SectionHeaderBinding by lazy {
        SectionHeaderBinding.bind(binding.root.findViewById(R.id.section_header_games))
    }

    private val sectionHeaderLastGames: SectionHeaderBinding by lazy {
        SectionHeaderBinding.bind(binding.root.findViewById(R.id.section_header_last_games))
    }

    companion object {
        private const val CROWN_THRESHOLD = 10_000
        private val DAY_LABELS = listOf("S", "M", "T", "W", "Th", "F", "S")
    }

    // ========== Update Methods ==========

    fun updateUserProgress(userProgress: UserProgress) {
        updateTopBar(userProgress)
        updateStats(userProgress)
        updateStreak(userProgress)
    }

    fun setupSectionHeaders(
        gamesTitle: String,
        lastGamesTitle: String,
        onAllGamesClick: () -> Unit,
        onAllLastGamesClick: () -> Unit
    ) {
        sectionHeaderGames.apply {
            tvSectionTitle.text = gamesTitle
            btnAll.setOnClickListener { onAllGamesClick() }
        }

        sectionHeaderLastGames.apply {
            tvSectionTitle.text = lastGamesTitle
            btnAll.setOnClickListener { onAllLastGamesClick() }
        }
    }

    // ========== Private Update Methods ==========

    private fun updateTopBar(userProgress: UserProgress) {
        topBar.apply {
            tvWelcome.text = context.getString(R.string.welcome_qurio_explorer)
            tvCharacterName.text = userProgress.selectedCharacter.capitalizeFirst()
            ivCharacter.loadCharacterImage(userProgress.selectedCharacter)
        }
    }

    private fun updateStats(userProgress: UserProgress) {
        stats.apply {
            tvLives.text = userProgress.lives.toString()
            tvCoins.text = formatNumber(userProgress.totalCoins)
            tvAwards.text = userProgress.awards.toString()
            ivCrown.isVisible = userProgress.totalCoins > CROWN_THRESHOLD
        }
    }

    private fun updateStreak(userProgress: UserProgress) {
        updateStreakText(userProgress.currentStreak)
        updateStreakDays(userProgress.streakDays)
    }

    private fun updateStreakText(currentStreak: Int) {
        streak.apply {
            if (currentStreak == 0) {
                streakTitle.text = context.getString(R.string.streak_start)
                streakSubtitle.text = context.getString(R.string.every_day_count)
            } else {
                streakTitle.text = context.getString(R.string.streak_count, currentStreak)
                streakSubtitle.text = context.getString(R.string.keep_it_up)
            }
        }
    }

    private fun updateStreakDays(streakDays: String) {
        val activeDays = parseActiveDays(streakDays)
        val dayViews = getDayViews()

        dayViews.forEachIndexed { index, (fireView, labelView) ->
            labelView.text = DAY_LABELS[index]
            fireView.isVisible = index in activeDays
        }
    }

    private fun parseActiveDays(streakDays: String): Set<Int> {
        return streakDays.split(",")
            .mapNotNull { it.toIntOrNull() }
            .toSet()
    }

    private fun getDayViews(): List<Pair<ImageView, TextView>> {
        return with(streak) {
            listOf(
                extractDayViews(daySunday.root),
                extractDayViews(dayMonday.root),
                extractDayViews(dayTuesday.root),
                extractDayViews(dayWednesday.root),
                extractDayViews(dayThursday.root),
                extractDayViews(dayFriday.root),
                extractDayViews(daySaturday.root)
            )
        }
    }

    private fun extractDayViews(dayView: android.view.View): Pair<ImageView, TextView> {
        val fireView = dayView.findViewById<ImageView>(R.id.ivDayFire)
        val labelView = dayView.findViewById<TextView>(R.id.tvDayLabel)
        return Pair(fireView, labelView)
    }

    private fun formatNumber(number: Int): String {
        return when {
            number >= 1000 -> String.format("%,d", number)
            else -> number.toString()
        }
    }
}