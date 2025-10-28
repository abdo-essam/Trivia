package com.qurio.trivia.presentation.ui.dialogs.achievements.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qurio.trivia.R
import com.qurio.trivia.databinding.ItemAchievementBinding
import com.qurio.trivia.domain.model.UserAchievement
import com.qurio.trivia.presentation.mapper.getIcon

class AchievementGridAdapter(
    private val onAchievementClick: (UserAchievement) -> Unit
) : ListAdapter<UserAchievement, AchievementGridAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAchievementBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onAchievementClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemAchievementBinding,
        private val onAchievementClick: (UserAchievement) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(userAchievement: UserAchievement) {
            binding.apply {
                val iconRes = userAchievement.achievement.getIcon(userAchievement.isUnlocked)
                ivAchievementBadge.setImageResource(iconRes)
                ivAchievementBadge.alpha = if (userAchievement.isUnlocked) 1.0f else 0.3f

                tvAchievementName.text = userAchievement.title
                tvAchievementName.setTextColor(
                    ContextCompat.getColor(
                        root.context,
                        if (userAchievement.isUnlocked) R.color.shade_primary else R.color.shade_tertiary
                    )
                )

                root.setOnClickListener { onAchievementClick(userAchievement) }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<UserAchievement>() {
        override fun areItemsTheSame(oldItem: UserAchievement, newItem: UserAchievement) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: UserAchievement, newItem: UserAchievement) =
            oldItem == newItem
    }
}