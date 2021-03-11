package com.krisgun.vibra.util

import kotlin.math.pow
import kotlin.math.sqrt
import com.github.psambit9791.jdsp.*

object SignalProcessing {

    /**
     * This function calculates the resulting total acceleration amplitude.
     */
    @JvmStatic
    fun totalAccelerationAmplitude(rawAccelerationData: List<Triple<Float, Float, Float>>): List<Float> {
        val result: MutableList<Float> = mutableListOf()
        rawAccelerationData.forEach { data ->
            result.add(
                    sqrt(
                            data.first.pow(2)
                            .plus(data.second.pow(2))
                            .plus(data.third.pow(2))
                    )
            )
        }
        return result
    }

    /**
     * Find the sign of the oscillation which gets lost in the total acceleration
     * calculation.
     */
    @JvmStatic
    fun findOscillationSign(totalAccelerationAmplitude: List<Float>) {

        /**
         * Find indices for the maxima in the acceleration vector
         */
        val lastListIndex = totalAccelerationAmplitude.size
        val f1 = totalAccelerationAmplitude.subList(1, lastListIndex - 1) //original signal
        val f2 = totalAccelerationAmplitude.subList(0, lastListIndex - 2) //left shift
        val f3 = totalAccelerationAmplitude.subList(2, lastListIndex) //right shift
        val threshold = 0.8

        val indexList: MutableList<Int> = mutableListOf()
        f1.forEachIndexed { index, f1Data ->
            if (f1Data > f2[index] && f1Data > f3[index] && f1Data > threshold) {
                indexList.add(index)
            }
        }
    }


}