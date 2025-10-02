package com.qurio.trivia.data.provider

import com.qurio.trivia.R
import com.qurio.trivia.data.model.Category
import com.qurio.trivia.data.model.Character
import com.qurio.trivia.utils.Constants

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
}