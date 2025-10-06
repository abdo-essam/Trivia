package com.qurio.trivia.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qurio.trivia.data.model.Category
import com.qurio.trivia.data.provider.CategoryColorProvider
import com.qurio.trivia.databinding.ItemGameCardBinding
import com.qurio.trivia.utils.extensions.setGradientBackground
import com.qurio.trivia.utils.extensions.setTintColor

class CategoryGameAdapter(
    private val onCategoryClick: (Category) -> Unit
) : ListAdapter<Category, CategoryGameAdapter.GameCardViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameCardViewHolder {
        val binding = ItemGameCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GameCardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GameCardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GameCardViewHolder(
        private val binding: ItemGameCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btnPlayContainer.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onCategoryClick(getItem(position))
                }
            }
        }

        fun bind(category: Category) {
            binding.apply {
                // Set category name and image
                tvCategoryName.text = category.displayName
                ivCategoryImage.setImageResource(category.imageRes)

                // Get colors for this category
                val borderColor = CategoryColorProvider.getBorderColor(category.id)

                // Apply border color
                ivCardBorder.setTintColor(borderColor)

                // Apply gradient overlay
                viewGradient.setGradientBackground(borderColor)
            }
        }
    }

    private class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Category, newItem: Category) =
            oldItem == newItem
    }
}