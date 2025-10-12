package com.qurio.trivia.domain.model

data class Character(
    val name: String,
    val displayName: String,
    val age: String,
    val description: String,
    val imageRes: Int,
    val lockedImageRes: Int,
    val unlockCost: Int,
    val isLocked: Boolean,
    val isSelected: Boolean = false
) {
    fun canBeUnlocked(userCoins: Int): Boolean {
        return isLocked && userCoins >= unlockCost
    }

    fun isAvailableForSelection(): Boolean {
        return !isLocked
    }
}