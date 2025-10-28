package com.qurio.trivia.domain.model

enum class Category(
    val id: Int,
    val categoryName: String,
    val displayName: String
) {
    GENERAL_KNOWLEDGE(
        id = 9,
        categoryName = "general",
        displayName = "General Knowledge"
    ),

    FILM_TV(
        id = 11,
        categoryName = "film",
        displayName = "Film & TV"
    ),

    MUSIC(
        id = 12,
        categoryName = "music",
        displayName = "Music"
    ),

    SCIENCE_NATURE(
        id = 17,
        categoryName = "science",
        displayName = "Science & Nature"
    ),

    FOOD_DRINK(
        id = 20,
        categoryName = "food",
        displayName = "Food & Drink"
    ),

    SPORTS(
        id = 21,
        categoryName = "sports",
        displayName = "Sport & Leisure"
    ),

    GEOGRAPHY(
        id = 22,
        categoryName = "geography",
        displayName = "Geography"
    ),

    HISTORY(
        id = 23,
        categoryName = "history",
        displayName = "History"
    ),

    ART_LITERATURE(
        id = 25,
        categoryName = "art",
        displayName = "Arts & Literature"
    ),

    SOCIETY_CULTURE(
        id = 27,
        categoryName = "culture",
        displayName = "Society & Culture"
    );

    companion object {
        /**
         * Find category by API ID
         */
        fun fromId(id: Int): Category? {
            return entries.find { it.id == id }
        }

        /**
         * Find category by name (case-insensitive)
         */
        fun fromName(name: String): Category? {
            return entries.find {
                it.categoryName.equals(name, ignoreCase = true) ||
                        it.displayName.equals(name, ignoreCase = true)
            }
        }

        /**
         * Get all categories as list
         */
        fun all(): List<Category> = entries

        /**
         * Get default category
         */
        fun default(): Category = GENERAL_KNOWLEDGE
    }
}