package com.qurio.trivia.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.qurio.trivia.databinding.ItemCategoryBinding
import com.qurio.trivia.domain.model.Category

/**
 * Adapter for displaying trivia categories in ViewPager2
 */
class CategoryAdapter(
    private val onCategoryClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var categories: List<Category> = emptyList()

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
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newList: List<Category>) {
        categories = newList
        notifyDataSetChanged()
    }

    class ViewHolder(
        private val binding: ItemCategoryBinding,
        private val onCategoryClick: (Category) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private var currentCategory: Category? = null

        init {
            // Access the button through the root view
            binding.root.findViewById<View>(com.qurio.trivia.R.id.btn_play_now)?.setOnClickListener {
                currentCategory?.let { category ->
                    onCategoryClick(category)
                }
            }
        }

        fun bind(category: Category) {
            currentCategory = category
            binding.apply {
                tvCategoryName.text = category.displayName
                ivCategoryImage.setImageResource(category.imageRes)
            }
        }
    }
}