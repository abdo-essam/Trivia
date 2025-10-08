package com.qurio.trivia.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qurio.trivia.R
import com.qurio.trivia.data.model.Character
import com.qurio.trivia.databinding.ItemCharacterGridBinding

class CharacterGridAdapter(
    private val onCharacterSelected: (Character) -> Unit,
    private val onLockedCharacterClick: (Character) -> Unit
) : ListAdapter<Character, CharacterGridAdapter.CharacterViewHolder>(CharacterDiffCallback()) {

    private var selectedPosition = 0 // First character selected by default

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

    fun selectCharacter(position: Int) {
        val previousPosition = selectedPosition
        selectedPosition = position
        notifyItemChanged(previousPosition)
        notifyItemChanged(selectedPosition)
    }

    inner class CharacterViewHolder(
        private val binding: ItemCharacterGridBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(character: Character, isSelected: Boolean) {
            binding.apply {
                ivCharacter.setImageResource(
                    if (character.isLocked) character.lockedImageRes
                    else character.imageRes
                )

                // Show selection checkmark only for selected unlocked character
                ivSelectedCheck.isVisible = isSelected && !character.isLocked

                // Set character name and color
                tvName.text = character.displayName
                tvName.setTextColor(
                    ContextCompat.getColor(
                        root.context,
                        when {
                            isSelected && !character.isLocked -> R.color.primary
                            character.isLocked -> R.color.shade_tertiary
                            else -> R.color.shade_secondary
                        }
                    )
                )

                // Handle clicks
                root.setOnClickListener {
                    if (character.isLocked) {
                        onLockedCharacterClick(character)
                    } else {
                        selectCharacter(bindingAdapterPosition)
                        onCharacterSelected(character)
                    }
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