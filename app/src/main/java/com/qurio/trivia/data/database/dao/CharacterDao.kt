package com.qurio.trivia.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.qurio.trivia.data.database.entity.CharacterEntity

@Dao
interface CharacterDao {

    @Query("SELECT * FROM characters")
    suspend fun getAllCharacters(): List<CharacterEntity>

    @Query("SELECT * FROM characters WHERE name = :name")
    suspend fun getCharacterByName(name: String): CharacterEntity?

    @Query("SELECT * FROM characters WHERE isUnlocked = 1")
    suspend fun getUnlockedCharacters(): List<CharacterEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: CharacterEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacters(characters: List<CharacterEntity>)

    @Update
    suspend fun updateCharacter(character: CharacterEntity)

    @Query("UPDATE characters SET isUnlocked = 1, unlockedAt = :unlockedAt WHERE name = :name")
    suspend fun unlockCharacter(name: String, unlockedAt: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM characters")
    suspend fun getCharacterCount(): Int

    @Query("DELETE FROM characters")
    suspend fun deleteAllCharacters()
}