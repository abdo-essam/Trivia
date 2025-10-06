/*
package com.qurio.trivia.ui.adapters

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qurio.trivia.R
import com.qurio.trivia.data.model.Category
import com.qurio.trivia.databinding.ItemGameCardBinding

class AllGamesAdapter(
    private val onCategoryClick: (Category) -> Unit
) : ListAdapter<Category, AllGamesAdapter.GameViewHolder>(GameDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        return GameViewHolder(
            ItemGameCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GameViewHolder(
        private val binding: ItemGameCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btnPlayNow.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onCategoryClick(getItem(position))
                }
            }

            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onCategoryClick(getItem(position))
                }
            }
        }

        fun bind(category: Category) {
            binding.apply {
                tvCategoryName.text = category.displayName
                ivCategoryImage.setImageResource(category.imageRes)

                // Set border color based on category
                setBorderColor(category)

                // Set gradient overlay based on category
                setGradientOverlay(category)
            }
        }

        private fun setBorderColor(category: Category) {
            val borderColor = when (category.id) {
                1 -> "#993397"  // Music - Purple
                2 -> "#FFA726"  // Food & Drink - Orange
                3 -> "#66BB6A"  // Geography - Green
                4 -> "#EF5350"  // General Knowledge - Red/Orange
                5 -> "#AB47BC"  // TV & Movies - Purple
                6 -> "#66BB6A"  // Politics - Green
                else -> "#993397"
            }

            // Create border programmatically
            val borderDrawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.TRANSPARENT)
                setStroke(6, Color.parseColor(borderColor))
                cornerRadius = 60f // 20dp converted to pixels approximately
            }

            binding.borderFrame.background = borderDrawable
        }

        private fun setGradientOverlay(category: Category) {
            val gradientColors = when (category.id) {
                1 -> intArrayOf( // Music - Pink to Purple
                    Color.parseColor("#00FFC0CB"),
                    Color.parseColor("#66993397")
                )
                2 -> intArrayOf( // Food & Drink - Light Yellow to Orange
                    Color.parseColor("#00FFF8DC"),
                    Color.parseColor("#66FFA726")
                )
                3 -> intArrayOf( // Geography - Light Green to Green
                    Color.parseColor("#00E8F5E9"),
                    Color.parseColor("#6666BB6A")
                )
                4 -> intArrayOf( // General Knowledge - Light Orange to Red
                    Color.parseColor("#00FFE0B2"),
                    Color.parseColor("#66EF5350")
                )
                5 -> intArrayOf( // TV & Movies - Light Purple
                    Color.parseColor("#00E1BEE7"),
                    Color.parseColor("#66AB47BC")
                )
                6 -> intArrayOf( // Politics - Light Green
                    Color.parseColor("#00E8F5E9"),
                    Color.parseColor("#6666BB6A")
                )
                else -> intArrayOf(
                    Color.parseColor("#00FFFFFF"),
                    Color.parseColor("#66000000")
                )
            }

            val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                gradientColors
            )

            binding.gradientOverlay.background = gradientDrawable
        }
    }

    private class GameDiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Category, newItem: Category) =
            oldItem == newItem
    }
}*/
