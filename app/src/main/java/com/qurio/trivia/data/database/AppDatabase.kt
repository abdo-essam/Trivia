package com.qurio.trivia.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.qurio.trivia.data.database.dao.CharacterDao
import com.qurio.trivia.data.database.dao.GameResultDao
import com.qurio.trivia.data.database.dao.UserProgressDao
import com.qurio.trivia.data.database.entity.CharacterEntity
import com.qurio.trivia.data.database.entity.GameResultEntity
import com.qurio.trivia.data.database.entity.UserProgressEntity

@Database(
    entities = [
        UserProgressEntity::class,
        GameResultEntity::class,
        CharacterEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameResultDao(): GameResultDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun characterDao(): CharacterDao

    companion object {
        const val DATABASE_NAME = "qurio_database"
    }
}