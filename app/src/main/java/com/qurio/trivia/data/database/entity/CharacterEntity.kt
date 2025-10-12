package com.qurio.trivia.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Stores only unlock state for characters
 * Character data comes from Character enum
 */
@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey
    val name: String,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null
)