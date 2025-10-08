package com.qurio.trivia.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qurio.trivia.R
import com.qurio.trivia.data.model.Character
import com.qurio.trivia.databinding.ItemCharacterGridBinding
import com.qurio.trivia.utils.extensions.loadCharacterImage

class CharacterGridAdapter(
    private val onCharacterClick: (Character) -> Unit,
    private val onLockedCharacterClick: ((Character) -> Unit)? = null
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
                // Load character image
                if (character.isLocked) {
                    ivCharacter.setImageResource(character.lockedImageRes)
                } else {
                    ivCharacter.loadCharacterImage(character.name)
                }

                // Show lock overlay for locked characters
                frameLock.isVisible = character.isLocked
                if (character.isLocked) {
                    tvCost.text = formatCost(character.unlockCost)
                }

                // Show selection indicator only for unlocked characters
                ivSelectedCheck.isVisible = isSelected && !character.isLocked

                // Character name color - primary for selected, tertiary for others
                tvName.text = character.displayName
                tvName.setTextColor(
                    root.context.getColor(
                        if (isSelected && !character.isLocked) R.color.primary
                        else R.color.shade_tertiary
                    )
                )

                // Click handling
                root.setOnClickListener {
                    if (character.isLocked) {
                        onLockedCharacterClick?.invoke(character)
                    } else {
                        val previousPosition = selectedPosition
                        selectedPosition = bindingAdapterPosition

                        notifyItemChanged(previousPosition)
                        notifyItemChanged(selectedPosition)

                        onCharacterClick(character)
                    }
                }
            }
        }

        private fun formatCost(cost: Int): String {
            return when {
                cost >= 1000 -> "${cost / 1000}k"
                else -> cost.toString()
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