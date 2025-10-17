package com.qurio.trivia.presentation.ui.dialogs.achievements.manager

import android.content.Context
import android.content.Intent
import com.qurio.trivia.R
import com.qurio.trivia.domain.model.UserAchievement

class AchievementShareManager(private val context: Context) {

    fun shareAchievement(achievement: UserAchievement) {
        val shareText = buildShareMessage(achievement)
        val shareIntent = createShareIntent(shareText)

        context.startActivity(
            Intent.createChooser(shareIntent, context.getString(R.string.share_achievement))
        )
    }

    private fun buildShareMessage(achievement: UserAchievement): String {
        return buildString {
            append("🏆 ")
            append(context.getString(R.string.achievement_unlocked_message, achievement.title))
            append("\n\n")
            append(achievement.description)
            append("\n\n")
            append(context.getString(R.string.can_you_unlock_it))
            append(" 🎮")
        }
    }

    private fun createShareIntent(shareText: String): Intent {
        return Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
    }
}