package com.qurio.trivia.data.provider

import com.qurio.trivia.R
import com.qurio.trivia.data.model.Category
import com.qurio.trivia.data.model.Character
import com.qurio.trivia.data.model.GameResult
import com.qurio.trivia.utils.Constants
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DataProvider {

    fun getCharacters(): List<Character> {
        return listOf(
            Character(
                name = "rika",
                displayName = "Rika",
                age = "Age: 12 Sunblooms",
                description = "Nature's little explorer! Rika talks to mushrooms and swears squirrels give her battle advice. Always ready for an adventure.",
                imageRes = R.drawable.character_rika,
                lockedImageRes = R.drawable.character_rika_locked
            ),
            Character(
                name = "kaiyo",
                displayName = "Kaiyo",
                age = "Age: 14 Storms",
                description = "A calm storm in human form. Kaiyo trains with ancient scrolls by day and drinks spicy tea by night. Sword sharp, heart sharper.",
                imageRes = R.drawable.character_kaiyo,
                lockedImageRes = R.drawable.character_kaiyo_locked
            ),
            Character(
                name = "mimi",
                displayName = "Mimi",
                age = "Age: 10 Volcano Puffs",
                description = "Tiny but terrifying! Mimi is always grumpy, but don't let that scare you—unless you like pranks involving firecrackers.",
                imageRes = R.drawable.character_mimi,
                lockedImageRes = R.drawable.character_mimi_locked
            ),
            Character(
                name = "yoru",
                displayName = "Yoru",
                age = "Age: 13 Shadows",
                description = "Quiet, mysterious, and probably watching you right now. Yoru shows up when you least expect it.",
                imageRes = R.drawable.character_yoru,
                lockedImageRes = R.drawable.character_yoru_locked
            ),
            Character(
                name = "kuro",
                displayName = "Kuro",
                age = "Age: 15 Thunder Beats",
                description = "Cool jacket, cooler moves. Kuro never backs down from a challenge.",
                imageRes = R.drawable.character_kuro,
                lockedImageRes = R.drawable.character_kuro_locked
            ),
            Character(
                name = "miko",
                displayName = "Miko",
                age = "Age: 11 Leaf Turns",
                description = "Energetic, cheerful, and faster than a leaf in the wind. Miko can turn any trivia into a giggle-fest.",
                imageRes = R.drawable.character_miko,
                lockedImageRes = R.drawable.character_miko_locked
            ),
            Character(
                name = "aori",
                displayName = "Aori",
                age = "Age: 13 Blade Echoes",
                description = "The sword chooses the wielder—and it chose Aori. Calm, focused.",
                imageRes = R.drawable.character_aori,
                lockedImageRes = R.drawable.character_aori_locked
            ),
            Character(
                name = "nara",
                displayName = "Nara",
                age = "Age: 12 Crystal Songs",
                description = "Part magic, part sass. Nara sparkles even when she's mad.",
                imageRes = R.drawable.character_nara,
                lockedImageRes = R.drawable.character_nara_locked
            ),
            Character(
                name = "renji",
                displayName = "Renji",
                age = "Age: 11 Hero Coins",
                description = "Small but mighty! Renji dreams of glory, carries a shield too big for him.",
                imageRes = R.drawable.character_renji,
                lockedImageRes = R.drawable.character_renji_locked
            )
        )
    }

    fun getCategories(): List<Category> {
        return listOf(
            Category(
                id = Constants.Categories.GEOGRAPHY,
                name = "geography",
                displayName = "Geography",
                imageRes = R.drawable.category_geography
            ),
            Category(
                id = Constants.Categories.SCIENCE_NATURE,
                name = "science",
                displayName = "Science",
                imageRes = R.drawable.category_science
            ),
            Category(
                id = Constants.Categories.GENERAL_KNOWLEDGE,
                name = "general",
                displayName = "General Knowledge",
                imageRes = R.drawable.category_general
            ),
            Category(
                id = Constants.Categories.MUSIC,
                name = "music",
                displayName = "Music",
                imageRes = R.drawable.category_music
            ),
            Category(
                id = Constants.Categories.FILM_TV,
                name = "film",
                displayName = "Film & TV",
                imageRes = R.drawable.category_film
            ),
            Category(
                id = Constants.Categories.FOOD_DRINK,
                name = "food",
                displayName = "Food & Drink",
                imageRes = R.drawable.category_food
            ),
            Category(
                id = Constants.Categories.SOCIETY_CULTURE,
                name = "culture",
                displayName = "Society & Culture",
                imageRes = R.drawable.category_culture
            ),
            Category(
                id = Constants.Categories.SPORTS,
                name = "sports",
                displayName = "Sport & Leisure",
                imageRes = R.drawable.category_sports
            ),
            Category(
                id = Constants.Categories.HISTORY,
                name = "history",
                displayName = "History",
                imageRes = R.drawable.category_history
            ),
            Category(
                id = Constants.Categories.ART_LITERATURE,
                name = "art",
                displayName = "Arts & Literature",
                imageRes = R.drawable.category_art
            )
        )
    }

    fun getFakeGameResults(): List<GameResult> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()

        return listOf(
            GameResult(
                id = 1,
                date = dateFormat.format(calendar.time), // Today: 03-08-2025
                category = "Science & Natural",
                difficulty = "Easy",
                totalQuestions = 10,
                correctAnswers = 8,
                incorrectAnswers = 2,
                skippedAnswers = 0,
                stars = 3,
                coins = 304,
                timeTaken = 93000 // 1m 33sec
            ),
            GameResult(
                id = 2,
                date = dateFormat.format(calendar.apply { add(Calendar.DAY_OF_MONTH, -2) }.time), // 01-08-2025
                category = "Mythology",
                difficulty = "Medium",
                totalQuestions = 10,
                correctAnswers = 4,
                incorrectAnswers = 6,
                skippedAnswers = 0,
                stars = 0,
                coins = -12,
                timeTaken = 56000 // 56sec
            ),
            GameResult(
                id = 3,
                date = dateFormat.format(calendar.apply { add(Calendar.DAY_OF_MONTH, -1) }.time), // 31-07-2025
                category = "Video Games",
                difficulty = "Hard",
                totalQuestions = 10,
                correctAnswers = 7,
                incorrectAnswers = 2,
                skippedAnswers = 1,
                stars = 0,
                coins = 304,
                timeTaken = 182000 // 3m 02sec
            ),
            GameResult(
                id = 4,
                date = dateFormat.format(calendar.apply { add(Calendar.DAY_OF_MONTH, -26) }.time), // 05-07-2025
                category = "Video Games",
                difficulty = "Easy",
                totalQuestions = 10,
                correctAnswers = 9,
                incorrectAnswers = 1,
                skippedAnswers = 0,
                stars = 1,
                coins = 568,
                timeTaken = 182000 // 3m 02sec
            ),
            GameResult(
                id = 5,
                date = dateFormat.format(calendar.apply { add(Calendar.DAY_OF_MONTH, -2) }.time), // 03-07-2025
                category = "Video Games",
                difficulty = "Medium",
                totalQuestions = 10,
                correctAnswers = 7,
                incorrectAnswers = 3,
                skippedAnswers = 0,
                stars = 0,
                coins = 304,
                timeTaken = 182000 // 3m 02sec
            ),
            GameResult(
                id = 6,
                date = "2025-06-28",
                category = "History",
                difficulty = "Easy",
                totalQuestions = 10,
                correctAnswers = 10,
                incorrectAnswers = 0,
                skippedAnswers = 0,
                stars = 3,
                coins = 450,
                timeTaken = 120000 // 2m
            ),
            GameResult(
                id = 7,
                date = "2025-06-25",
                category = "Geography",
                difficulty = "Medium",
                totalQuestions = 10,
                correctAnswers = 5,
                incorrectAnswers = 5,
                skippedAnswers = 0,
                stars = 1,
                coins = 120,
                timeTaken = 150000 // 2m 30sec
            ),
            GameResult(
                id = 8,
                date = "2025-06-20",
                category = "Music",
                difficulty = "Hard",
                totalQuestions = 10,
                correctAnswers = 3,
                incorrectAnswers = 7,
                skippedAnswers = 0,
                stars = 0,
                coins = -50,
                timeTaken = 200000 // 3m 20sec
            ),
            GameResult(
                id = 9,
                date = "2025-06-18",
                category = "Food & Drink",
                difficulty = "Easy",
                totalQuestions = 10,
                correctAnswers = 8,
                incorrectAnswers = 1,
                skippedAnswers = 1,
                stars = 2,
                coins = 280,
                timeTaken = 95000 // 1m 35sec
            ),
            GameResult(
                id = 10,
                date = "2025-06-15",
                category = "General Knowledge",
                difficulty = "Medium",
                totalQuestions = 10,
                correctAnswers = 6,
                incorrectAnswers = 4,
                skippedAnswers = 0,
                stars = 1,
                coins = 180,
                timeTaken = 165000 // 2m 45sec
            )
        )
    }
}