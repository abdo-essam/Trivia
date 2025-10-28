package com.qurio.trivia.presentation.ui.dialogs.achievements.manager

import androidx.recyclerview.widget.GridLayoutManager
import com.qurio.trivia.databinding.DialogAchievementsBinding
import com.qurio.trivia.domain.model.UserAchievement
import com.qurio.trivia.presentation.ui.dialogs.achievements.adapter.AchievementGridAdapter

class AchievementsUIManager(
    private val binding: DialogAchievementsBinding,
    private val adapter: AchievementGridAdapter
) {

    fun setupRecyclerView() {
        binding.achievementsRecyclerView.apply {
            layoutManager = GridLayoutManager(context, GRID_SPAN_COUNT)
            this.adapter = this@AchievementsUIManager.adapter
            setHasFixedSize(true)
            post { requestLayout() }
        }
    }

    fun displayAchievements(achievements: List<UserAchievement>) {
        binding.achievementsRecyclerView.post {
            adapter.submitList(achievements)
        }
    }

    companion object {
        private const val GRID_SPAN_COUNT = 4
    }
}