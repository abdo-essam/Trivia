package com.qurio.trivia.presentation.mapper

import com.qurio.trivia.R
import com.qurio.trivia.domain.model.Category

fun Category.imageRes(): Int = when (this) {
    Category.GENERAL_KNOWLEDGE -> R.drawable.category_general
    Category.FILM_TV -> R.drawable.category_film
    Category.MUSIC -> R.drawable.category_music
    Category.SCIENCE_NATURE -> R.drawable.category_science
    Category.FOOD_DRINK -> R.drawable.category_food
    Category.SPORTS -> R.drawable.category_sports
    Category.GEOGRAPHY -> R.drawable.category_geography
    Category.HISTORY -> R.drawable.category_history
    Category.ART_LITERATURE -> R.drawable.category_art
    Category.SOCIETY_CULTURE -> R.drawable.category_culture
}

fun Category.borderColorRes(): Int = when (this) {
    Category.MUSIC, Category.FILM_TV -> R.color.secondary
    Category.FOOD_DRINK, Category.SPORTS, Category.ART_LITERATURE -> R.color.primary
    Category.GEOGRAPHY, Category.SCIENCE_NATURE -> R.color.green
    Category.GENERAL_KNOWLEDGE, Category.HISTORY, Category.SOCIETY_CULTURE -> R.color.orange
}

fun Category.gradientColorRes(): Int = when (this) {
    Category.MUSIC, Category.FILM_TV -> R.color.secondary
    Category.FOOD_DRINK, Category.SPORTS, Category.ART_LITERATURE -> R.color.primary
    Category.GEOGRAPHY, Category.SCIENCE_NATURE -> R.color.green
    Category.GENERAL_KNOWLEDGE, Category.HISTORY, Category.SOCIETY_CULTURE -> R.color.orange
}