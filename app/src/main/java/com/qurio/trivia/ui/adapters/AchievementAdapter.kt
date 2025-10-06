package com.qurio.trivia.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qurio.trivia.R
import com.qurio.trivia.data.model.Achievement
import com.qurio.trivia.databinding.ItemAchievementBinding

class AchievementAdapter(
    private val onAchievementClick: (Achievement) -> Unit
) : ListAdapter<Achievement, AchievementAdapter.AchievementViewHolder>(AchievementDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val binding = ItemAchievementBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AchievementViewHolder(binding, onAchievementClick)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AchievementViewHolder(
        private val binding: ItemAchievementBinding,
        private val onAchievementClick: (Achievement) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(achievement: Achievement) {
            binding.apply {
                tvTitle.text = achievement.title
                tvDescription.text = achievement.description

                // Set icon based on unlock status
                val iconRes = if (achievement.isUnlocked) {
                    achievement.iconRes
                } else {
                    achievement.iconLockedRes
                }
                ivIcon.setImageResource(iconRes)

                // Show/hide progress
                val showProgress = achievement.maxProgress > 1
                progressBar.isVisible = showProgress
                tvProgress.isVisible = showProgress

                if (showProgress) {
                    tvProgress.text = "${achievement.progress}/${achievement.maxProgress}"
                    progressBar.max = achievement.maxProgress
                    progressBar.progress = achievement.progress
                }

                // Show unlocked state
                if (achievement.isUnlocked) {
                    ivIcon.alpha = 1.0f
                    tvTitle.setTextColor(root.context.getColor(R.color.white))
                    ivUnlocked.isVisible = true
                } else {
                    ivIcon.alpha = 0.5f
                    tvTitle.setTextColor(root.context.getColor(R.color.shade_tertiary))
                    ivUnlocked.isVisible = false
                }

                // Click listener
                root.setOnClickListener {
                    onAchievementClick(achievement)
                }
            }
        }
    }

    private class AchievementDiffCallback : DiffUtil.ItemCallback<Achievement>() {
        override fun areItemsTheSame(oldItem: Achievement, newItem: Achievement): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Achievement, newItem: Achievement): Boolean {
            return oldItem == newItem
        }
    }
}