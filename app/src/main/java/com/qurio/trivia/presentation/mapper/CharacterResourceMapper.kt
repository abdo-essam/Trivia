package com.qurio.trivia.presentation.mapper

import androidx.annotation.DrawableRes
import com.qurio.trivia.R
import com.qurio.trivia.domain.model.Character

/**
 * Extension functions for mapping characters to UI resources
 * Belongs in presentation layer as it contains Android-specific resources
 */

/**
 * Get character image resource (unlocked state)
 */
@DrawableRes
fun Character.imageRes(): Int = when (this) {
    Character.RIKA -> R.drawable.character_rika
    Character.KAIYO -> R.drawable.character_kaiyo
    Character.MIMI -> R.drawable.character_mimi
    Character.YORU -> R.drawable.character_yoru
    Character.KURO -> R.drawable.character_kuro
    Character.MIKO -> R.drawable.character_miko
    Character.AORI -> R.drawable.character_aori
    Character.NARA -> R.drawable.character_nara
    Character.RENJI -> R.drawable.character_renji
}

/**
 * Get character info/detail image resource
 */
@DrawableRes
fun Character.infoImageRes(): Int = when (this) {
    Character.RIKA -> R.drawable.rika_info
    Character.KAIYO -> R.drawable.kaiyo_info
    Character.MIMI -> R.drawable.mimi_info
    Character.YORU -> R.drawable.yoru_info
    Character.KURO -> R.drawable.kuro_info
    Character.MIKO -> R.drawable.miko_info
    Character.AORI -> R.drawable.aori_info
    Character.NARA -> R.drawable.nara_info
    Character.RENJI -> R.drawable.renji_info
}

/**
 * Get character locked image resource
 */
@DrawableRes
fun Character.lockedImageRes(): Int = when (this) {
    Character.RIKA -> R.drawable.character_rika // Rika is never locked
    Character.KAIYO -> R.drawable.character_kaiyo_locked
    Character.MIMI -> R.drawable.character_mimi_locked
    Character.YORU -> R.drawable.character_yoru_locked
    Character.KURO -> R.drawable.character_kuro_locked
    Character.MIKO -> R.drawable.character_miko_locked
    Character.AORI -> R.drawable.character_aori_locked
    Character.NARA -> R.drawable.character_nara_locked
    Character.RENJI -> R.drawable.character_renji_locked
}

/**
 * Get appropriate character image based on unlock status
 */
@DrawableRes
fun Character.getImage(isUnlocked: Boolean): Int =
    if (isUnlocked) imageRes() else lockedImageRes()