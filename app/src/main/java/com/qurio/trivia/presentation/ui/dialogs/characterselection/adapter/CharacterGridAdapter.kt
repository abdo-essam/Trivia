package com.qurio.trivia.presentation.ui.dialogs.characterselection.adapter

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
import com.qurio.trivia.domain.repository.CharacterRepository
import com.qurio.trivia.presentation.mapper.imageRes
import com.qurio.trivia.presentation.mapper.lockedImageRes

/**
 * Adapter for displaying characters in a grid with lock status
 */
class CharacterGridAdapter(
    private val onCharacterSelected: (CharacterRepository.CharacterWithStatus) -> Unit,
    private val onLockedCharacterClick: (Character) -> Unit
) : ListAdapter<CharacterRepository.CharacterWithStatus, CharacterGridAdapter.ViewHolder>(DiffCallback()) {

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
        currentList.forEachIndexed { index, item ->
            if (item.character.characterName == oldSelection ||
                item.character.characterName == characterName) {
                notifyItemChanged(index)
            }
        }
    }

    class ViewHolder(
        private val binding: ItemCharacterGridBinding,
        private val onCharacterSelected: (CharacterRepository.CharacterWithStatus) -> Unit,
        private val onLockedCharacterClick: (Character) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: CharacterRepository.CharacterWithStatus,
            selectedCharacterName: String?
        ) {
            val character = item.character
            val isSelected = character.characterName == selectedCharacterName && item.isUnlocked

            binding.apply {
                // Character image
                ivCharacter.setImageResource(
                    if (item.isUnlocked) character.imageRes()
                    else character.lockedImageRes()
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
                                !item.isUnlocked -> R.color.shade_tertiary
                                else -> R.color.shade_secondary
                            }
                        )
                    )
                }

                // Click handling
                root.setOnClickListener {
                    if (item.isUnlocked) {
                        onCharacterSelected(item)
                    } else {
                        onLockedCharacterClick(character)
                    }
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<CharacterRepository.CharacterWithStatus>() {
        override fun areItemsTheSame(
            oldItem: CharacterRepository.CharacterWithStatus,
            newItem: CharacterRepository.CharacterWithStatus
        ) = oldItem.character.characterName == newItem.character.characterName

        override fun areContentsTheSame(
            oldItem: CharacterRepository.CharacterWithStatus,
            newItem: CharacterRepository.CharacterWithStatus
        ) = oldItem == newItem
    }
}