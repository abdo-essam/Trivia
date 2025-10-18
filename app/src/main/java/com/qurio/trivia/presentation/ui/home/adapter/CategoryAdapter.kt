package com.qurio.trivia.presentation.ui.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.qurio.trivia.R
import com.qurio.trivia.databinding.ItemCategoryBinding
import com.qurio.trivia.domain.model.Category
import com.qurio.trivia.presentation.mapper.borderColorRes
import com.qurio.trivia.presentation.mapper.gradientColorRes
import com.qurio.trivia.presentation.mapper.imageRes

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

        fun bind(category: Category) {

            binding.apply {
                tvCategoryName.text = category.displayName

                categoryCard.apply {
                    setCardImage(category.imageRes())
                    setCardBorderColor(category.borderColorRes())
                    setCardGradientColor(category.gradientColorRes())
                }
                root.findViewById<View>(R.id.btn_play_now)?.setOnClickListener {
                    onCategoryClick(category)
                }
            }
        }
    }
}