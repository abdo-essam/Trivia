package com.qurio.trivia.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qurio.trivia.R
import com.qurio.trivia.databinding.ItemCharacterGridBinding
import com.qurio.trivia.domain.model.Character

/**
 * Adapter for displaying characters in a grid
 */
class CharacterGridAdapter(
    private val onCharacterSelected: (Character) -> Unit,
    private val onLockedCharacterClick: (Character) -> Unit
) : ListAdapter<Character, CharacterGridAdapter.ViewHolder>(DiffCallback()) {

    private var selectedCharacterName: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCharacterGridBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onCharacterSelected,
            onLockedCharacterClick
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), selectedCharacterName)
    }

    /**
     * Update selected character and refresh UI
     */
    fun setSelectedCharacter(characterName: String) {
        val oldSelection = selectedCharacterName
        selectedCharacterName = characterName

        // Refresh only affected items
        currentList.forEachIndexed { index, character ->
            if (character.name == oldSelection || character.name == characterName) {
                notifyItemChanged(index)
            }
        }
    }

    class ViewHolder(
        private val binding: ItemCharacterGridBinding,
        private val onCharacterSelected: (Character) -> Unit,
        private val onLockedCharacterClick: (Character) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(character: Character, selectedCharacterName: String?) {
            val isSelected = character.name == selectedCharacterName && !character.isLocked

            binding.apply {
                // Character image
                ivCharacter.setImageResource(
                    if (character.isLocked) {
                        character.lockedImageRes
                    } else {
                        character.imageRes
                    }
                )

                // Selection indicator
                ivSelectedCheck.isVisible = isSelected

                // Character name styling
                tvName.apply {
                    text = character.displayName
                    setTextColor(
                        ContextCompat.getColor(
                            context,
                            when {
                                isSelected -> R.color.primary
                                character.isLocked -> R.color.shade_tertiary
                                else -> R.color.shade_secondary
                            }
                        )
                    )
                }

                // Click handling
                root.setOnClickListener {
                    if (character.isLocked) {
                        onLockedCharacterClick(character)
                    } else {
                        onCharacterSelected(character)
                    }
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Character>() {
        override fun areItemsTheSame(oldItem: Character, newItem: Character) =
            oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: Character, newItem: Character) =
            oldItem == newItem
    }
}