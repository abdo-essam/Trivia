package com.qurio.trivia.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qurio.trivia.R
import com.qurio.trivia.databinding.ItemAchievementBinding
import com.qurio.trivia.domain.model.AchievementState
import com.qurio.trivia.presentation.mapper.getIcon

class AchievementGridAdapter(
    private val onAchievementClick: (AchievementState) -> Unit
) : ListAdapter<AchievementState, AchievementGridAdapter.ViewHolder>(DiffCallback()) {

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
        private val onAchievementClick: (AchievementState) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(state: AchievementState) {
            binding.apply {
                // Set icon based on unlock status
                val iconRes = state.achievement.getIcon(state.isUnlocked)
                ivAchievementBadge.setImageResource(iconRes)
                ivAchievementBadge.alpha = if (state.isUnlocked) 1.0f else 0.3f

                // Set name and color based on Figma design
                tvAchievementName.text = state.title
                tvAchievementName.setTextColor(
                    ContextCompat.getColor(
                        root.context,
                        if (state.isUnlocked) R.color.shade_primary else R.color.shade_tertiary
                    )
                )

                root.setOnClickListener { onAchievementClick(state) }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<AchievementState>() {
        override fun areItemsTheSame(oldItem: AchievementState, newItem: AchievementState) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: AchievementState, newItem: AchievementState) =
            oldItem == newItem
    }
}