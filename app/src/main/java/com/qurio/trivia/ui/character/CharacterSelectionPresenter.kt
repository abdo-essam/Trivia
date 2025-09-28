package com.qurio.trivia.ui.character

import com.qurio.trivia.base.BasePresenter
import com.qurio.trivia.data.database.UserProgressDao
import com.qurio.trivia.data.model.Character
import com.qurio.trivia.data.provider.DataProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class CharacterSelectionPresenter @Inject constructor(
    private val userProgressDao: UserProgressDao
) : BasePresenter<CharacterSelectionView>() {

    fun loadCharacters() {
        val characters = DataProvider.getCharacters()
        view?.displayCharacters(characters)
    }

    fun selectCharacter(character: Character) {
        CoroutineScope(Dispatchers.IO).launch {
            userProgressDao.updateSelectedCharacter(character.name)

            CoroutineScope(Dispatchers.Main).launch {
                view?.navigateToDifficultySelection()
            }
        }
    }
}