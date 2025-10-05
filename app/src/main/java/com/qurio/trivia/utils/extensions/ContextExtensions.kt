package com.qurio.trivia.utils.extensions

import android.content.Context
import com.qurio.trivia.utils.NetworkUtils

fun Context.isNetworkAvailable(): Boolean {
    return NetworkUtils.isNetworkAvailable(this)
}