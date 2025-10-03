package com.qurio.trivia.utils.extensions

import java.util.Locale

fun String.capitalizeFirst(): String =
    replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault())
        else it.toString()
    }