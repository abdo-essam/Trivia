package com.qurio.trivia.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qurio.trivia.R
import com.qurio.trivia.data.model.Achievement
import com.qurio.trivia.databinding.ItemAchievementBinding

class AchievementAdapter : ListAdapter<Achievement, AchievementAdapter.AchievementViewHolder>(AchievementDiffCallback()) {

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

    class AchievementViewHolder(
        private val binding: ItemAchievementBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(achievement: Achievement) {
            binding.tvTitle.text = achievement.title
            binding.tvDescription.text = achievement.description
            binding.ivIcon.setImageResource(achievement.iconRes)

            // Show progress
            if (achievement.maxProgress > 1) {
                binding.tvProgress.text = "${achievement.progress}/${achievement.maxProgress}"
                binding.progressBar.max = achievement.maxProgress
                binding.progressBar.progress = achievement.progress
                binding.tvProgress.visibility = android.view.View.VISIBLE
                binding.progressBar.visibility = android.view.View.VISIBLE
            } else {
                binding.tvProgress.visibility = android.view.View.GONE
                binding.progressBar.visibility = android.view.View.GONE
            }

            // Show unlocked state
            if (achievement.isUnlocked) {
                binding.ivIcon.alpha = 1.0f
                binding.tvTitle.setTextColor(binding.root.context.getColor(R.color.white))
                binding.ivUnlocked.visibility = android.view.View.VISIBLE
            } else {
                binding.ivIcon.alpha = 0.5f
                binding.tvTitle.setTextColor(binding.root.context.getColor(R.color.gray_dark))
                binding.ivUnlocked.visibility = android.view.View.GONE
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