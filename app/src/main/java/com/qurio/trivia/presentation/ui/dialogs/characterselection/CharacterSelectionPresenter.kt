package com.qurio.trivia.presentation.ui.dialogs.characterselection

import android.util.Log
import com.qurio.trivia.domain.model.Character
import com.qurio.trivia.domain.repository.CharacterRepository
import com.qurio.trivia.presentation.base.BasePresenter
import javax.inject.Inject

class CharacterSelectionPresenter @Inject constructor(
    private val characterRepository: CharacterRepository
) : BasePresenter<CharacterSelectionView>() {

    companion object {
        private const val TAG = "CharacterSelectionPresenter"
    }

    fun loadCharacters() {
        tryToExecute(
            execute = {
                characterRepository.getAllCharactersWithUnlockStatus()
            },
            onSuccess = { charactersWithStatus ->
                Log.d(TAG, "✓ Loaded ${charactersWithStatus.size} characters")
                withView { displayCharacters(charactersWithStatus) }
            },
            onError = { error ->
                Log.e(TAG, "✗ Failed to load characters", error)
                withView { showError("Failed to load characters") }
            },
            showLoading = true
        )
    }

    fun saveSelectedCharacter(character: Character) {
        Log.d(TAG, "Saving character selection: ${character.displayName}")

        tryToExecute(
            execute = {
                characterRepository.selectCharacter(character)
                character
            },
            onSuccess = { selectedCharacter ->
                Log.d(TAG, "✓ Character saved: ${selectedCharacter.displayName}")
                withView { onCharacterSaved(selectedCharacter) }
            },
            onError = { error ->
                Log.e(TAG, "✗ Failed to save character", error)
                withView { showError("Failed to save character") }
            },
            showLoading = true
        )
    }
}