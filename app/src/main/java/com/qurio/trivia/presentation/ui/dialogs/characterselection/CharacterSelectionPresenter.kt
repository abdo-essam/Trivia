package com.qurio.trivia.presentation.ui.dialogs.characterselection

import com.qurio.trivia.domain.model.Character
import com.qurio.trivia.domain.repository.CharacterRepository
import com.qurio.trivia.presentation.base.BasePresenter
import javax.inject.Inject

class CharacterSelectionPresenter @Inject constructor(
    private val characterRepository: CharacterRepository
) : BasePresenter<CharacterSelectionView>() {

    fun loadCharacters() {
        tryToExecute(
            execute = {
                characterRepository.getAllCharactersWithUnlockStatus()
            },
            onSuccess = { charactersWithStatus ->
                withView { displayCharacters(charactersWithStatus) }
            },
            onError = { error ->
                withView { showError("Failed to load characters") }
            },
            showLoading = true
        )
    }

    fun saveSelectedCharacter(character: Character) {

        tryToExecute(
            execute = {
                characterRepository.selectCharacter(character)
                character
            },
            onSuccess = { selectedCharacter ->
                withView { onCharacterSaved(selectedCharacter) }
            },
            onError = { error ->
                withView { showError("Failed to save character") }
            },
            showLoading = true
        )
    }
}