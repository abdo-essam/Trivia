package com.qurio.trivia.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qurio.trivia.databinding.ItemCategoryBinding
import com.qurio.trivia.domain.model.Category

/**
 * Adapter for displaying trivia categories
 */
class CategoryAdapter(
    private val onCategoryClick: (Category) -> Unit
) : ListAdapter<Category, CategoryAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onCategoryClick
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemCategoryBinding,
        private val onCategoryClick: (Category) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // Get item from adapter
                }
            }

            binding.btnPlayNow.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // Handled in bind
                }
            }
        }

        fun bind(category: Category) {
            binding.apply {
                tvCategoryName.text = category.displayName
                ivCategoryImage.setImageResource(category.imageRes)

                btnPlayNow.setOnClickListener {
                    onCategoryClick(category)
                }

                root.setOnClickListener {
                    onCategoryClick(category)
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Category, newItem: Category) =
            oldItem == newItem
    }
}