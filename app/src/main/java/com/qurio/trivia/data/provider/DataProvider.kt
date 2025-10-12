package com.qurio.trivia.data.provider

import com.qurio.trivia.R
import com.qurio.trivia.domain.model.Category
import com.qurio.trivia.utils.Constants

object DataProvider {
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