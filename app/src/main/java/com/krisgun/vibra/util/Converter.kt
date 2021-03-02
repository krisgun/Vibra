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
