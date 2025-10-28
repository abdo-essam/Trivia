package com.qurio.trivia.utils.extensions

import android.annotation.SuppressLint
import java.text.DecimalFormat

/**
 * Format number with thousand separators
 */
fun Int.formatWithCommas(): String {
    return when {
        this >= 1000 -> DecimalFormat("#,###").format(this)
        else -> this.toString()
    }
}

/**
 * Format number in compact form (e.g., 1.5K, 2.3M)
 */
@SuppressLint("DefaultLocale")
fun Int.formatCompact(): String {
    return when {
        this >= 1_000_000 -> String.format("%.1fM", this / 1_000_000.0)
        this >= 1_000 -> String.format("%.1fK", this / 1_000.0)
        else -> this.toString()
    }
}