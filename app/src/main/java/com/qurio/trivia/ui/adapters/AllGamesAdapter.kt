package com.qurio.trivia.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qurio.trivia.data.model.Category
import com.qurio.trivia.databinding.ItemGameCardBinding

class AllGamesAdapter(
    private val onCategoryClick: (Category) -> Unit
) : ListAdapter<Category, AllGamesAdapter.GameViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val binding = ItemGameCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GameViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GameViewHolder(
        private val binding: ItemGameCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.apply {
                tvCategoryName.text = category.displayName
                ivCategoryImage.setImageResource(category.imageRes)

                // Click on "Play Now" button
                btnPlayNow.setOnClickListener {
                    onCategoryClick(category)
                }
            }
        }
    }

    private class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }
}