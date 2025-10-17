package com.qurio.trivia.presentation.ui.result.managers

import android.content.Context
import android.content.Intent
import com.qurio.trivia.R
import com.qurio.trivia.presentation.ui.result.model.GameResultStats
import com.qurio.trivia.utils.Constants

class GameResultShareManager(private val context: Context) {

    fun shareResults(categoryName: String, stats: GameResultStats) {
        val shareText = buildShareMessage(categoryName, stats)
        val shareIntent = createShareIntent(shareText)

        context.startActivity(
            Intent.createChooser(
                shareIntent,
                context.getString(R.string.share_with_friends)
            )
        )
    }

    private fun buildShareMessage(categoryName: String, stats: GameResultStats): String {
        return if (stats.isWon) {
            context.getString(
                R.string.share_win_message,
                categoryName,
                stats.correct,
                Constants.QUESTIONS_PER_GAME
            )
        } else {
            context.getString(
                R.string.share_lose_message,
                categoryName
            )
        }
    }

    private fun createShareIntent(shareText: String): Intent {
        return Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
        }
    }
}