package com.qurio.trivia.presentation.ui.home.managers

import android.content.Context
import com.qurio.trivia.R
import com.qurio.trivia.databinding.*
import com.qurio.trivia.domain.model.UserProgress

class HomeUIManager(
    private val binding: FragmentHomeBinding,
    private val context: Context
) {
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

    private val topBarManager by lazy {
        TopBarManager(topBarBinding, context)
    }

    private val statsManager by lazy {
        StatsManager(statsBinding)
    }

    private val streakManager by lazy {
        StreakManager(streakBinding, context)
    }

    private val gamesHeaderManager by lazy {
        SectionHeaderManager(sectionHeaderGamesBinding)
    }

    private val lastGamesHeaderManager by lazy {
        SectionHeaderManager(sectionHeaderLastGamesBinding)
    }

    fun updateUserProgress(userProgress: UserProgress) {
        topBarManager.update(userProgress)
        statsManager.update(userProgress)
        streakManager.update(userProgress)
    }

    fun updateUnlockedAchievements(unlockedCount: Int) {
        statsManager.updateAchievements(unlockedCount)
    }

    fun setupSectionHeaders(
        gamesTitle: String,
        lastGamesTitle: String,
        onAllGamesClick: () -> Unit,
        onAllLastGamesClick: () -> Unit
    ) {
        gamesHeaderManager.setup(gamesTitle, onAllGamesClick)
        lastGamesHeaderManager.setup(lastGamesTitle, onAllLastGamesClick)
    }

    fun setupClickListeners(
        onSettingsClick: () -> Unit,
        onCharacterClick: () -> Unit,
        onLivesClick: () -> Unit,
        onAwardsClick: () -> Unit
    ) {
        topBarManager.setOnSettingsClickListener(onSettingsClick)
        topBarManager.setOnCharacterClickListener(onCharacterClick)
        statsManager.setOnLivesClickListener(onLivesClick)
        statsManager.setOnAwardsClickListener(onAwardsClick)
    }
}