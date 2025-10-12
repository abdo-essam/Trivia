package com.qurio.trivia.presentation.ui.home.updaters

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.qurio.trivia.R
import com.qurio.trivia.databinding.ItemStreakBinding
import com.qurio.trivia.domain.model.UserProgress

/**
 * Handles streak section UI updates
 */
class StreakUpdater(
    private val binding: ItemStreakBinding,
    private val context: Context
) {
    private val dayLabels: Array<String> by lazy {
        context.resources.getStringArray(R.array.day_labels_short)
    }

    fun update(userProgress: UserProgress) {
        val hasStreak = userProgress.currentStreak > 0

        updateStreakText(userProgress.currentStreak)
        updateStreakDays(userProgress.streakDays, hasStreak)
    }

    private fun updateStreakText(currentStreak: Int) {
        binding.apply {
            if (currentStreak == 0) {
                streakTitle.text = context.getString(R.string.streak_start)
                streakSubtitle.text = context.getString(R.string.every_day_count)
            } else {
                streakTitle.text = context.getString(R.string.streak_count, currentStreak)
                streakSubtitle.text = context.getString(R.string.keep_it_up)
            }
        }
    }

    private fun updateStreakDays(streakDays: String, hasStreak: Boolean) {
        val activeDays = if (hasStreak) parseActiveDays(streakDays) else emptySet()
        val dayViews = getDayViews()

        dayViews.forEachIndexed { index, (fireIcon, labelView) ->
            if (hasStreak) {
                // Has streak: show fire icon for active days, hide labels
                fireIcon.isVisible = index in activeDays
                labelView.isVisible = false
            } else {
                // No streak: hide all fire icons, show labels
                fireIcon.isVisible = false
                labelView.isVisible = true
                labelView.text = dayLabels[index]
            }
        }
    }

    private fun parseActiveDays(streakDays: String): Set<Int> {
        return streakDays
            .split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .toSet()
    }

    private fun getDayViews(): List<Pair<ImageView, TextView>> {
        return binding.run {
            listOf(
                daySunday.root,
                dayMonday.root,
                dayTuesday.root,
                dayWednesday.root,
                dayThursday.root,
                dayFriday.root,
                daySaturday.root
            ).map { extractDayViews(it) }
        }
    }

    private fun extractDayViews(dayView: View): Pair<ImageView, TextView> {
        return Pair(
            dayView.findViewById(R.id.ivDayFire),
            dayView.findViewById(R.id.tvDayLabel)
        )
    }
}