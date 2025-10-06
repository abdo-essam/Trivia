package com.qurio.trivia.data.provider

import androidx.core.graphics.toColorInt
import com.qurio.trivia.utils.Constants

object CategoryColorProvider {

    fun getBorderColor(categoryId: Int): Int {
        return when (categoryId) {
            Constants.Categories.MUSIC -> "#993397".toColorInt()  // Music - Purple (12)
            Constants.Categories.FOOD_DRINK -> "#FFA726".toColorInt()  // Food & Drink - Orange (20)
            Constants.Categories.GEOGRAPHY -> "#66BB6A".toColorInt()  // Geography - Green (22)
            Constants.Categories.GENERAL_KNOWLEDGE -> "#EF5350".toColorInt()  // General Knowledge - Red (9)
            Constants.Categories.FILM_TV -> "#AB47BC".toColorInt()  // Film & TV - Purple variant (11)
            Constants.Categories.SCIENCE_NATURE -> "#2196F3".toColorInt()  // Science - Blue (17)
            Constants.Categories.HISTORY -> "#FF9800".toColorInt()  // History - Orange (23)
            Constants.Categories.SPORTS -> "#4CAF50".toColorInt()  // Sports - Green (21)
            Constants.Categories.ART_LITERATURE -> "#E91E63".toColorInt()  // Art - Pink (25)
            Constants.Categories.SOCIETY_CULTURE -> "#9C27B0".toColorInt()  // Culture - Purple (27)
            else -> "#993397".toColorInt()
        }
    }

    fun getGradientColor(categoryId: Int): Int {
        return when (categoryId) {
            Constants.Categories.MUSIC -> "#993397".toColorInt()  // Music - Purple
            Constants.Categories.FOOD_DRINK -> "#FFA726".toColorInt()  // Food & Drink - Orange
            Constants.Categories.GEOGRAPHY -> "#66BB6A".toColorInt()  // Geography - Green
            Constants.Categories.GENERAL_KNOWLEDGE -> "#EF5350".toColorInt()  // General Knowledge - Red
            Constants.Categories.FILM_TV -> "#AB47BC".toColorInt()  // Film & TV - Purple
            Constants.Categories.SCIENCE_NATURE -> "#2196F3".toColorInt()  // Science - Blue
            Constants.Categories.HISTORY -> "#FF9800".toColorInt()  // History - Orange
            Constants.Categories.SPORTS -> "#4CAF50".toColorInt()  // Sports - Green
            Constants.Categories.ART_LITERATURE -> "#E91E63".toColorInt()  // Art - Pink
            Constants.Categories.SOCIETY_CULTURE -> "#9C27B0".toColorInt()  // Culture - Purple
            else -> "#993397".toColorInt()
        }
    }
}