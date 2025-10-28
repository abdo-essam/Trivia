package com.qurio.trivia.presentation.ui.dialogs.characterselection.manager

import androidx.recyclerview.widget.GridLayoutManager
import com.qurio.trivia.databinding.DialogCharacterSelectionBinding
import com.qurio.trivia.domain.model.Character
import com.qurio.trivia.domain.repository.CharacterRepository
import com.qurio.trivia.presentation.ui.dialogs.characterselection.adapter.CharacterGridAdapter

class CharacterSelectionUIManager(
    private val binding: DialogCharacterSelectionBinding,
    private val adapter: CharacterGridAdapter
) {

    fun setupRecyclerView() {
        binding.rvCharacters.apply {
            layoutManager = GridLayoutManager(context, GRID_SPAN_COUNT)
            this.adapter = this@CharacterSelectionUIManager.adapter
            setHasFixedSize(true)
            post { requestLayout() }
        }
    }

    fun displayCharacters(characters: List<CharacterRepository.CharacterWithStatus>) {
        binding.rvCharacters.post {
            adapter.submitList(characters)
        }
    }

    fun updateConfirmButtonState(isEnabled: Boolean) {
        binding.btnConfirm.apply {
            this.isEnabled = isEnabled
            alpha = if (isEnabled) 1.0f else 0.5f
        }
    }

    fun setLoadingState(isLoading: Boolean) {
        binding.apply {
            btnConfirm.isEnabled = !isLoading
            btnCancel.isEnabled = !isLoading
        }
    }

    fun selectCharacter(character: Character) {
        adapter.setSelectedCharacter(character.characterName)
        updateConfirmButtonState(true)
    }

    companion object {
        private const val GRID_SPAN_COUNT = 5
    }
}