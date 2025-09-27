package com.qurio.trivia.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.qurio.trivia.data.model.GameResult
import com.qurio.trivia.data.model.UserProgress

@Database(
    entities = [GameResult::class, UserProgress::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameResultDao(): GameResultDao
    abstract fun userProgressDao(): UserProgressDao

    companion object {
        const val DATABASE_NAME = "qurio_database"
    }
}