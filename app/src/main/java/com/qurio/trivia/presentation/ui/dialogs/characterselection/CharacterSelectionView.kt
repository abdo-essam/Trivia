package com.qurio.trivia.presentation.ui.dialogs.characterselection

import com.qurio.trivia.presentation.base.BaseView
import com.qurio.trivia.data.model.Character

interface CharacterSelectionView : BaseView {
    fun displayCharacters(characters: List<Character>)
    fun onCharacterSaved()
}