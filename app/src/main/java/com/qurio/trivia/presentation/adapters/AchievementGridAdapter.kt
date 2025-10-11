package com.qurio.trivia.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qurio.trivia.R
import com.qurio.trivia.data.model.Achievement
import com.qurio.trivia.databinding.ItemAchievementBinding

class AchievementGridAdapter(
    private val onAchievementClick: (Achievement) -> Unit
) : ListAdapter<Achievement, AchievementGridAdapter.AchievementViewHolder>(AchievementDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val binding = ItemAchievementBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AchievementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AchievementViewHolder(
        private val binding: ItemAchievementBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(achievement: Achievement) {
            binding.apply {
                // Set icon based on unlock status
                val iconRes = if (achievement.isUnlocked) {
                    achievement.iconRes
                } else {
                    achievement.iconLockedRes
                }
                ivAchievementBadge.setImageResource(iconRes)

                // Set alpha for locked achievements
                ivAchievementBadge.alpha = if (achievement.isUnlocked) 1.0f else 0.3f

                // Set name and color
                tvAchievementName.text = achievement.title
                tvAchievementName.setTextColor(
                    ContextCompat.getColor(
                        root.context,
                        if (achievement.isUnlocked) R.color.white else R.color.shade_tertiary
                    )
                )

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