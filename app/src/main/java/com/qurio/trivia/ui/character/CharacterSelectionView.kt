package com.qurio.trivia.ui.character

import com.qurio.trivia.base.BaseView
import com.qurio.trivia.data.model.Character

interface CharacterSelectionView : BaseView {
    fun displayCharacters(characters: List<Character>)
    fun navigateToDifficultySelection()
}