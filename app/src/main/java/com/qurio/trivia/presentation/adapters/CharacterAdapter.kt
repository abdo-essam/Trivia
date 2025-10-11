package com.qurio.trivia.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.qurio.trivia.data.model.Character
import com.qurio.trivia.databinding.ItemCharacterBinding

class CharacterAdapter(
    private val onCharacterSelected: (Character, Boolean) -> Unit
) : ListAdapter<Character, CharacterAdapter.CharacterViewHolder>(CharacterDiffCallback()) {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val binding = ItemCharacterBinding.inflate(
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
        private val binding: ItemCharacterBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(character: Character, isSelected: Boolean) {
            binding.tvCharacterName.text = character.displayName
            binding.tvCharacterAge.text = character.age
            binding.tvCharacterDescription.text = character.description

            // Load character image
            Glide.with(binding.ivCharacter.context)
                .load(character.imageRes)
                //.placeholder(R.drawable.placeholder_character)
                .into(binding.ivCharacter)

            // Show selection state
            binding.root.isSelected = isSelected
            binding.viewSelectionBorder.visibility = if (isSelected) {
                View.VISIBLE
            } else {
                View.GONE
            }

            binding.root.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = bindingAdapterPosition

                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)

                onCharacterSelected(character, true)
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