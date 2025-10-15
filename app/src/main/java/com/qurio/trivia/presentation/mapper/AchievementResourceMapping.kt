package com.qurio.trivia.presentation.mapper

import androidx.annotation.DrawableRes
import com.qurio.trivia.R
import com.qurio.trivia.domain.model.Achievement

/**
 * Extension functions for mapping achievements to UI resources
 * This belongs in presentation layer as it contains Android-specific resources
 */

@DrawableRes
fun Achievement.iconRes(): Int = when (this) {
    Achievement.QUIZ_ROOKIE -> R.drawable.ic_achievement_quiz_rookie
    Achievement.STREAK_STARTER -> R.drawable.ic_achievement_streak_starter
    Achievement.LUCKY_GUESS -> R.drawable.ic_achievement_lucky_guess
    Achievement.EXPLORER -> R.drawable.ic_achievement_explorer
    Achievement.TRIVIA_CHAMP -> R.drawable.ic_achievement_trivia_champ
    Achievement.COLLECTOR -> R.drawable.ic_achievement_collector
    Achievement.LEGEND -> R.drawable.ic_achievement_legend
    Achievement.UNTOUCHABLE -> R.drawable.ic_achievement_untouchable
    Achievement.QUICK_THINKER -> R.drawable.ic_achievement_quick_thinker
    Achievement.COLLECTOR_MASTER -> R.drawable.ic_achievement_collector_gold
    Achievement.LUCKY_GUESS_MASTER -> R.drawable.ic_achievement_lucky_guess_gold
}

@DrawableRes
fun Achievement.iconLockedRes(): Int = when (this) {
    Achievement.QUIZ_ROOKIE -> R.drawable.ic_achievement_quiz_rookie_locked
    Achievement.STREAK_STARTER -> R.drawable.ic_achievement_streak_starter_locked
    Achievement.LUCKY_GUESS -> R.drawable.ic_achievement_lucky_guess_locked
    Achievement.EXPLORER -> R.drawable.ic_achievement_explorer_locked
    Achievement.TRIVIA_CHAMP -> R.drawable.ic_achievement_trivia_champ_locked
    Achievement.COLLECTOR -> R.drawable.ic_achievement_collector_locked
    Achievement.LEGEND -> R.drawable.ic_achievement_legend_locked
    Achievement.UNTOUCHABLE -> R.drawable.ic_achievement_untouchable_locked
    Achievement.QUICK_THINKER -> R.drawable.ic_achievement_quick_thinker_locked
    Achievement.COLLECTOR_MASTER -> R.drawable.ic_achievement_collector_gold_locked
    Achievement.LUCKY_GUESS_MASTER -> R.drawable.ic_achievement_lucky_guess_gold_locked
}

@DrawableRes
fun Achievement.getIcon(isUnlocked: Boolean): Int =
    if (isUnlocked) iconRes() else iconLockedRes()