package com.qurio.trivia.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qurio.trivia.data.model.Character
import com.qurio.trivia.databinding.ItemCharacterGridBinding
import com.qurio.trivia.utils.extensions.loadCharacterImage

class CharacterGridAdapter(
    private val onCharacterClick: (Character) -> Unit
) : ListAdapter<Character, CharacterGridAdapter.CharacterViewHolder>(CharacterDiffCallback()) {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val binding = ItemCharacterGridBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CharacterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        holder.bind(getItem(position), position == selectedPosition)
    }

    inner class CharacterViewHolder(
        private val binding: ItemCharacterGridBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(character: Character, isSelected: Boolean) {
            binding.apply {
                tvName.text = character.displayName
                ivCharacter.loadCharacterImage(character.name)

                // Show selection indicator
                ivSelectedCheck.isVisible = isSelected

                // Set alpha for locked characters (optional)
                val alpha = if (character.name == "locked") 0.5f else 1.0f
                ivCharacter.alpha = alpha

                root.setOnClickListener {
                    val previousPosition = selectedPosition
                    selectedPosition = bindingAdapterPosition

                    notifyItemChanged(previousPosition)
                    notifyItemChanged(selectedPosition)

                    onCharacterClick(character)
                }
            }
        }
    }

    private class CharacterDiffCallback : DiffUtil.ItemCallback<Character>() {
        override fun areItemsTheSame(oldItem: Character, newItem: Character): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Character, newItem: Character): Boolean {
            return oldItem == newItem
        }
    }
}