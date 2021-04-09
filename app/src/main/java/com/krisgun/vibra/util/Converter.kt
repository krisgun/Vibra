@file:JvmName("Converter")
package com.krisgun.vibra.util

import java.util.Locale
import kotlin.math.log10

object Converter {
    @JvmStatic
    fun durationToPaddedSeconds(seconds: Int): String {
        val s: Int = seconds % 60
        return if (s > 9) {
            "$s"
        } else {
            "0$s"
        }
    }

    @JvmStatic
    fun durationToPaddedMinutes(seconds: Int): String {
        val m: Int = (seconds / 60)
        return if (m > 9) {
            "$m"
        } else {
            "0$m"
        }
    }

    @JvmStatic
    fun accelerationToString(value: Float): String {
        return "${value.format(2)} m/s^2"
    }

    @JvmStatic
    fun gyroToString(value: Float): String {
        return "${value.format(2)} rad/s"
    }

    @JvmStatic
    fun frequencyToString(value: Double): String {
        return "${value.format(2)} Hz"
    }

    @JvmStatic
    fun Double.format(digits: Int) = "%.${digits}f".format(this)

    @JvmStatic
    fun Float.format(digits: Int) = "%.${digits}f".format(this)


    /**
     * Add converter for date string and duration string
     */

    @JvmStatic
    fun durationToString(duration: Int): String {
        val seconds: String = (duration % 60).toString()
        val minutes: String = (duration / 60).toString()

        return when {
            (duration  == 0) -> "$seconds seconds"
            (duration == 1) -> "$seconds second"
            (duration in 2..59) -> "$seconds seconds"
            (duration % 60 == 0 && duration > 60) -> "$minutes minutes"
            (duration % 60 == 0) -> "$minutes minute"
            else -> "$minutes min $seconds sec"
        }
    }

    @JvmStatic
    fun detailsFrequencyToString(value: Double): String {
        return String.format(Locale.US, "%.2f Hz", value)
    }

    @JvmStatic
    fun dataPointsToString(value: Int): String {
        return "$value data points"
    }

    @JvmStatic
    fun amplitudeSpectrumResonanceToString(dataPoint: Pair<Double, Double>?): String {
        return if (dataPoint != null) {
            "${dataPoint.first.format(3)} Hz: ${dataPoint.second.format(3)}"
        } else "Calculating..."
    }

    @JvmStatic
    fun powerSpectrumResonanceToString(dataPoint: Pair<Double, Double>?): String {
        return if (dataPoint != null) {
            "${dataPoint.first.format(3)} Hz: ${(10*log10(dataPoint.second)).format(3)} dB"
        } else
            "Calculating..."
    }
}
