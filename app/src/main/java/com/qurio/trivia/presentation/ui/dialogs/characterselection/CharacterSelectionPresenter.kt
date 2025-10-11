package com.qurio.trivia.presentation.ui.dialogs.characterselection

import android.util.Log
import com.qurio.trivia.presentation.base.BasePresenter
import com.qurio.trivia.data.repository.CharacterRepository
import javax.inject.Inject

class CharacterSelectionPresenter @Inject constructor(
    private val repository: CharacterRepository
) : BasePresenter<CharacterSelectionView>() {

    // ========== Load Characters ==========

    fun loadCharacters() {
        tryToExecute(
            execute = {
                repository.getAllCharacters()
            },
            onSuccess = { characters ->
                Log.d(TAG, "Loaded ${characters.size} characters")
                withView { displayCharacters(characters) }
            },
            onError = { error ->
                Log.e(TAG, "Error loading characters", error)
                withView { showError("Failed to load characters") }
            },
            showLoading = false
        )
    }

    // ========== Save Selected Character ==========

    fun saveSelectedCharacter(characterName: String) {
        tryToExecute(
            execute = {
                repository.selectCharacter(characterName)
            },
            onSuccess = {
                Log.d(TAG, "Character $characterName saved")
                withView { onCharacterSaved() }
            },
            onError = { error ->
                Log.e(TAG, "Error saving character", error)
                withView { showError("Failed to save character") }
            },
            showLoading = true
        )
    }

    companion object {
        private const val TAG = "CharacterSelectionPresenter"
    }
}