package com.qurio.trivia.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey
    val name: String,
    val displayName: String,
    val age: String,
    val description: String,
    val imageRes: Int,
    val lockedImageRes: Int,
    val unlockCost: Int,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null
)