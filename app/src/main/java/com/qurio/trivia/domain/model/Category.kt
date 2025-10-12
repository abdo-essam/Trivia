package com.qurio.trivia.domain.model

import com.qurio.trivia.R

/**
 * All available trivia categories
 * IDs match Open Trivia Database API
 */
enum class Category(
    val id: Int,
    val categoryName: String,
    val displayName: String,
    val imageRes: Int
) {
    GENERAL_KNOWLEDGE(
        id = 9,
        categoryName = "general",
        displayName = "General Knowledge",
        imageRes = R.drawable.category_general
    ),

    FILM_TV(
        id = 11,
        categoryName = "film",
        displayName = "Film & TV",
        imageRes = R.drawable.category_film
    ),

    MUSIC(
        id = 12,
        categoryName = "music",
        displayName = "Music",
        imageRes = R.drawable.category_music
    ),

    SCIENCE_NATURE(
        id = 17,
        categoryName = "science",
        displayName = "Science & Nature",
        imageRes = R.drawable.category_science
    ),

    FOOD_DRINK(
        id = 20,
        categoryName = "food",
        displayName = "Food & Drink",
        imageRes = R.drawable.category_food
    ),

    SPORTS(
        id = 21,
        categoryName = "sports",
        displayName = "Sport & Leisure",
        imageRes = R.drawable.category_sports
    ),

    GEOGRAPHY(
        id = 22,
        categoryName = "geography",
        displayName = "Geography",
        imageRes = R.drawable.category_geography
    ),

    HISTORY(
        id = 23,
        categoryName = "history",
        displayName = "History",
        imageRes = R.drawable.category_history
    ),

    ART_LITERATURE(
        id = 25,
        categoryName = "art",
        displayName = "Arts & Literature",
        imageRes = R.drawable.category_art
    ),

    SOCIETY_CULTURE(
        id = 27,
        categoryName = "culture",
        displayName = "Society & Culture",
        imageRes = R.drawable.category_culture
    );

    companion object {
        /**
         * Find category by API ID
         */
        fun fromId(id: Int): Category? {
            return values().find { it.id == id }
        }

        /**
         * Find category by name (case-insensitive)
         */
        fun fromName(name: String): Category? {
            return values().find {
                it.categoryName.equals(name, ignoreCase = true)
            }
        }

        /**
         * Get all categories as list
         */
        fun all(): List<Category> = values().toList()

        /**
         * Get default category
         */
        fun default(): Category = GENERAL_KNOWLEDGE
    }
}