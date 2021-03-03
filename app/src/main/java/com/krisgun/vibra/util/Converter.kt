@file:JvmName("Converter")
package com.krisgun.vibra.util


fun durationToPaddedSeconds(seconds: Int): String {
    val s: Int  = seconds % 60
    return if (s > 9) {
        "$s"
    } else {
        "0$s"
    }
}

fun durationToPaddedMinutes(seconds: Int): String {
    val m: Int = (seconds / 60)
    return if (m > 9) {
        "$m"
    } else {
        "0$m"
    }
}


/**
 * Add converter for date string and duration string
 */

fun durationToString(duration: Int ): String {
    val seconds: String = (duration % 60).toString()
    val minutes: String = (duration / 60).toString()

    return when {
        (duration in 2..59) -> "$seconds seconds"
        (duration < 60) -> "$seconds second"
        (duration % 60 == 0 && duration > 60) -> "$minutes minutes"
        (duration % 60 == 0) -> "$minutes minute"
        else -> "$minutes min $seconds sec"
    }
}