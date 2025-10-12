package com.qurio.trivia.data.database

import android.util.Log
import com.qurio.trivia.R
import com.qurio.trivia.data.database.dao.CharacterDao
import com.qurio.trivia.data.database.dao.GameResultDao
import com.qurio.trivia.data.database.dao.UserProgressDao
import com.qurio.trivia.data.database.entity.CharacterEntity
import com.qurio.trivia.data.database.entity.UserProgressEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSeeder @Inject constructor(
    private val userProgressDao: UserProgressDao,
    private val characterDao: CharacterDao,
    private val gameResultDao: GameResultDao
) {

    companion object {
        private const val TAG = "DatabaseSeeder"
    }

    suspend fun seedDatabase() = withContext(Dispatchers.IO) {
        try {
            // Seed user progress
            seedUserProgress()

            // Seed characters
            seedCharacters()


            Log.d(TAG, "✓ Database seeded successfully")
        } catch (e: Exception) {
            Log.e(TAG, "✗ Error seeding database", e)
        }
    }

    private suspend fun seedUserProgress() {
        if (userProgressDao.getUserProgress() == null) {
            userProgressDao.insertOrUpdateUserProgress(
                UserProgressEntity(
                    id = 1,
                    lives = 4,
                    totalCoins = 50000, // Give some coins for testing
                    awards = 0,
                    selectedCharacter = "rika",
                    soundEnabled = true,
                    musicEnabled = true,
                    currentStreak = 0,
                    lastPlayedDate = "",
                    streakDays = ""
                )
            )
            Log.d(TAG, "✓ User progress seeded")
        }
    }

    private suspend fun seedCharacters() {
        if (characterDao.getCharacterCount() == 0) {
            val characters = listOf(
                CharacterEntity(
                    name = "rika",
                    displayName = "Rika",
                    age = "Age: 12 Sunblooms",
                    description = "Nature's little explorer! Rika talks to mushrooms and swears squirrels give her battle advice. Always ready for an adventure.",
                    imageRes = R.drawable.character_rika,
                    lockedImageRes = R.drawable.character_rika_locked,
                    unlockCost = 0,
                    isUnlocked = true, // Default character
                    unlockedAt = System.currentTimeMillis()
                ),
                CharacterEntity(
                    name = "kaiyo",
                    displayName = "Kaiyo",
                    age = "Age: 14 Storms",
                    description = "A calm storm in human form. Kaiyo trains with ancient scrolls by day and drinks spicy tea by night. Sword sharp, heart sharper.",
                    imageRes = R.drawable.character_kaiyo,
                    lockedImageRes = R.drawable.character_kaiyo_locked,
                    unlockCost = 300,
                    isUnlocked = false
                ),
                CharacterEntity(
                    name = "mimi",
                    displayName = "Mimi",
                    age = "Age: 10 Volcano Puffs",
                    description = "Tiny but terrifying! Mimi is always grumpy, but don't let that scare you—unless you like pranks involving firecrackers.",
                    imageRes = R.drawable.character_mimi,
                    lockedImageRes = R.drawable.character_mimi_locked,
                    unlockCost = 700,
                    isUnlocked = false
                ),
                CharacterEntity(
                    name = "yoru",
                    displayName = "Yoru",
                    age = "Age: 13 Shadows",
                    description = "Quiet, mysterious, and probably watching you right now. Yoru shows up when you least expect it.",
                    imageRes = R.drawable.character_yoru,
                    lockedImageRes = R.drawable.character_yoru_locked,
                    unlockCost = 1000,
                    isUnlocked = false
                ),
                CharacterEntity(
                    name = "kuro",
                    displayName = "Kuro",
                    age = "Age: 15 Thunder Beats",
                    description = "Cool jacket, cooler moves. Kuro never backs down from a challenge.",
                    imageRes = R.drawable.character_kuro,
                    lockedImageRes = R.drawable.character_kuro_locked,
                    unlockCost = 3000,
                    isUnlocked = false
                ),
                CharacterEntity(
                    name = "miko",
                    displayName = "Miko",
                    age = "Age: 11 Leaf Turns",
                    description = "Energetic, cheerful, and faster than a leaf in the wind. Miko can turn any trivia into a giggle-fest.",
                    imageRes = R.drawable.character_miko,
                    lockedImageRes = R.drawable.character_miko_locked,
                    unlockCost = 7000,
                    isUnlocked = false
                ),
                CharacterEntity(
                    name = "aori",
                    displayName = "Aori",
                    age = "Age: 13 Blade Echoes",
                    description = "The sword chooses the wielder—and it chose Aori. Calm, focused.",
                    imageRes = R.drawable.character_aori,
                    lockedImageRes = R.drawable.character_aori_locked,
                    unlockCost = 12000,
                    isUnlocked = false
                ),
                CharacterEntity(
                    name = "nara",
                    displayName = "Nara",
                    age = "Age: 12 Crystal Songs",
                    description = "Part magic, part sass. Nara sparkles even when she's mad.",
                    imageRes = R.drawable.character_nara,
                    lockedImageRes = R.drawable.character_nara_locked,
                    unlockCost = 30000,
                    isUnlocked = false
                ),
                CharacterEntity(
                    name = "renji",
                    displayName = "Renji",
                    age = "Age: 11 Hero Coins",
                    description = "Small but mighty! Renji dreams of glory, carries a shield too big for him.",
                    imageRes = R.drawable.character_renji,
                    lockedImageRes = R.drawable.character_renji_locked,
                    unlockCost = 50000,
                    isUnlocked = false
                )
            )

            characterDao.insertCharacters(characters)
            Log.d(TAG, "✓ ${characters.size} characters seeded")
        }
    }
}