package com.qurio.trivia.presentation.ui.games.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qurio.trivia.R
import com.qurio.trivia.databinding.ItemGameCardBinding
import com.qurio.trivia.domain.model.Category
import com.qurio.trivia.presentation.mapper.imageRes

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
                ivCategoryImage.setImageResource(category.imageRes())

                root.findViewById<View>(R.id.btn_play_now)?.setOnClickListener {
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