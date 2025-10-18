package com.qurio.trivia.domain.model

enum class Category(
    val id: Int,
    val categoryName: String,
    val displayName: String
) {
    GENERAL_KNOWLEDGE(9, "general", "General Knowledge"),
    FILM_TV(11, "film", "Film & TV"),
    MUSIC(12, "music", "Music"),
    SCIENCE_NATURE(17, "science", "Science & Nature"),
    FOOD_DRINK(20, "food", "Food & Drink"),
    SPORTS(21, "sports", "Sport & Leisure"),
    GEOGRAPHY(22, "geography", "Geography"),
    HISTORY(23, "history", "History"),
    ART_LITERATURE(25, "art", "Arts & Literature"),
    SOCIETY_CULTURE(27, "culture", "Society & Culture");

    companion object {
        fun all(): List<Category> = entries
        fun default(): Category = GENERAL_KNOWLEDGE
    }
}