package com.qurio.trivia.presentation.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qurio.trivia.data.model.GameResult
import com.qurio.trivia.databinding.ItemLastGameBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class LastGamesAdapter() : ListAdapter<GameResult, LastGamesAdapter.LastGameViewHolder>(LastGameDiffCallback()) {

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
                // Format date
                tvDate.text = formatDate(gameResult.date)

                // Set category
                tvCategory.text = gameResult.category

                // Set coins with color based on value
                val coinsValue = gameResult.coins
                tvCoins.text = if (coinsValue >= 0) {
                    coinsValue.toString()
                } else {
                    coinsValue.toString() // Will include the minus sign
                }
                tvCoins.setTextColor(
                    if (coinsValue >= 0) {
                        Color.parseColor("#DEFFFFFF") // shade_primary
                    } else {
                        Color.parseColor("#E12F32") // red_accent
                    }
                )

                // Set stars
                tvStars.text = gameResult.stars.toString()

                // Format time
                tvTime.text = formatTime(gameResult.timeTaken)
            }
        }

        private fun formatDate(dateString: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val date = inputFormat.parse(dateString)
                date?.let { outputFormat.format(it) } ?: dateString
            } catch (e: Exception) {
                dateString
            }
        }

        private fun formatTime(timeInMillis: Long): String {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillis) % 60

            return if (minutes > 0) {
                "${minutes}m ${seconds}sec"
            } else {
                "${seconds}sec"
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