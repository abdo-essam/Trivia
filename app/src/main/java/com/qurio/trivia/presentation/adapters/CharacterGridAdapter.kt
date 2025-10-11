package com.qurio.trivia.presentation.adapters

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

    private var selectedCharacterName: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val binding = ItemCharacterGridBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CharacterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * Set selected character and refresh UI
     */
    fun setSelectedCharacter(characterName: String) {
        val previousSelection = selectedCharacterName
        selectedCharacterName = characterName

        // Refresh both old and new selection
        currentList.forEachIndexed { index, character ->
            if (character.name == previousSelection || character.name == characterName) {
                notifyItemChanged(index)
            }
        }
    }

    inner class CharacterViewHolder(
        private val binding: ItemCharacterGridBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(character: Character) {
            val isSelected = character.name == selectedCharacterName && !character.isLocked

            binding.apply {
                // Character image
                ivCharacter.setImageResource(
                    if (character.isLocked) character.lockedImageRes else character.imageRes
                )

                // Selection check mark
                ivSelectedCheck.isVisible = isSelected

                // Character name and color
                tvName.text = character.displayName
                tvName.setTextColor(
                    ContextCompat.getColor(
                        root.context,
                        when {
                            isSelected -> R.color.primary
                            character.isLocked -> R.color.shade_tertiary
                            else -> R.color.shade_secondary
                        }
                    )
                )

                // Click listener
                root.setOnClickListener {
                    if (character.isLocked) {
                        onLockedCharacterClick(character)
                    } else {
                        setSelectedCharacter(character.name)
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