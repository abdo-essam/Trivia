package com.qurio.trivia.utils.extensions

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.qurio.trivia.R

fun View.setVisibleIf(condition: Boolean) {
    visibility = if (condition) View.VISIBLE else View.GONE
}

fun ViewGroup.removeAllViewsSafely() {
    try {
        removeAllViews()
    } catch (e: Exception) {
        // Handle exception if views are being removed during animation
    }
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun ImageView.loadCharacterImage(characterName: String) {
    setImageResource(characterName.toCharacterDrawable())
}

@DrawableRes
fun String.toCharacterDrawable(): Int = when (lowercase()) {
    "rika" -> R.drawable.character_rika
    "kaiyo" -> R.drawable.character_kaiyo
    "mimi" -> R.drawable.character_mimi
    "yoru" -> R.drawable.character_yoru
    "kuro" -> R.drawable.character_kuro
    "miko" -> R.drawable.character_miko
    "aori" -> R.drawable.character_aori
    "nara" -> R.drawable.character_nara
    "renji" -> R.drawable.character_renji
    else -> R.drawable.character_rika
}