package com.qurio.trivia.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey
    val name: String,
    val isUnlocked: Boolean = false,
)