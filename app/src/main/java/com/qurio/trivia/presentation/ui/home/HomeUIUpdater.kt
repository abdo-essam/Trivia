package com.qurio.trivia.presentation.ui.home

import android.content.Context
import com.qurio.trivia.R
import com.qurio.trivia.databinding.*
import com.qurio.trivia.domain.model.UserProgress
import com.qurio.trivia.presentation.ui.home.updaters.*

/**
 * Coordinator for Home screen UI updates
 * Delegates to specialized updaters for each section
 */
class HomeUIUpdater(
    private val binding: FragmentHomeBinding,
    private val context: Context
) {
    // Binding references
    private val topBarBinding: TopBarHomeBinding by lazy {
        TopBarHomeBinding.bind(binding.root.findViewById(R.id.top_bar_home))
    }

    private val statsBinding: ItemStatsBinding by lazy {
        ItemStatsBinding.bind(binding.root.findViewById(R.id.layout_stats))
    }

    private val streakBinding: ItemStreakBinding by lazy {
        ItemStreakBinding.bind(binding.root.findViewById(R.id.layout_streak))
    }

    private val sectionHeaderGamesBinding: SectionHeaderBinding by lazy {
        SectionHeaderBinding.bind(binding.root.findViewById(R.id.section_header_games))
    }

    private val sectionHeaderLastGamesBinding: SectionHeaderBinding by lazy {
        SectionHeaderBinding.bind(binding.root.findViewById(R.id.section_header_last_games))
    }

    // Specialized updaters
    private val topBarUpdater by lazy {
        TopBarUpdater(topBarBinding, context)
    }

    private val statsUpdater by lazy {
        StatsUpdater(statsBinding)
    }

    private val streakUpdater by lazy {
        StreakUpdater(streakBinding, context)
    }

    private val gamesHeaderUpdater by lazy {
        SectionHeaderUpdater(sectionHeaderGamesBinding)
    }

    private val lastGamesHeaderUpdater by lazy {
        SectionHeaderUpdater(sectionHeaderLastGamesBinding)
    }

    // ========== Public API ==========

    /**
     * Update all user progress related UI
     */
    fun updateUserProgress(userProgress: UserProgress) {
        topBarUpdater.update(userProgress)
        statsUpdater.update(userProgress)
        streakUpdater.update(userProgress)
    }

    /**
     * Setup section headers with titles and click listeners
     */
    fun setupSectionHeaders(
        gamesTitle: String,
        lastGamesTitle: String,
        onAllGamesClick: () -> Unit,
        onAllLastGamesClick: () -> Unit
    ) {
        gamesHeaderUpdater.setup(gamesTitle, onAllGamesClick)
        lastGamesHeaderUpdater.setup(lastGamesTitle, onAllLastGamesClick)
    }

    /**
     * Setup click listeners for interactive elements
     */
    fun setupClickListeners(
        onSettingsClick: () -> Unit,
        onCharacterClick: () -> Unit,
        onLivesClick: () -> Unit,
        onAwardsClick: () -> Unit
    ) {
        topBarUpdater.setOnSettingsClickListener(onSettingsClick)
        topBarUpdater.setOnCharacterClickListener(onCharacterClick)
        statsUpdater.setOnLivesClickListener(onLivesClick)
        statsUpdater.setOnAwardsClickListener(onAwardsClick)
    }
}