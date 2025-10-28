package com.qurio.trivia.presentation.ui.lastgames.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qurio.trivia.R
import com.qurio.trivia.databinding.ItemLastGameBinding
import com.qurio.trivia.domain.model.GameResult
import com.qurio.trivia.utils.DateUtils

class LastGamesAdapter : ListAdapter<GameResult, LastGamesAdapter.LastGameViewHolder>(LastGameDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LastGameViewHolder {
        val binding = ItemLastGameBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LastGameViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LastGameViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class LastGameViewHolder(
        private val binding: ItemLastGameBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(gameResult: GameResult) {
            binding.apply {
                tvDate.text = DateUtils.formatDate(gameResult.date)
                tvCategory.text = gameResult.category

                val coinsValue = gameResult.coins
                tvCoins.text = DateUtils.formatCoins(coinsValue)
                tvCoins.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        if (coinsValue >= 0) R.color.coins_positive else R.color.coins_negative
                    )
                )

                tvStars.text = gameResult.stars.toString()
                tvTime.text = DateUtils.formatTime(gameResult.timeTaken)
            }
        }
    }

    private class LastGameDiffCallback : DiffUtil.ItemCallback<GameResult>() {
        override fun areItemsTheSame(oldItem: GameResult, newItem: GameResult): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GameResult, newItem: GameResult): Boolean {
            return oldItem == newItem
        }
    }
}