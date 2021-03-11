package com.krisgun.vibra.util

import android.util.Log
import kotlin.math.sqrt
import kotlin.math.pow
import kotlin.math.sign

private const val TAG = "SignalProcessing"

class SignalProcessing {

    companion object {

        /**
         * This function calculates the resulting total acceleration amplitude.
         */
        fun totalAccelerationAmplitude(rawData: List<Pair<Float, Triple<Float, Float, Float>>>): List<Float> {

            val totAccResult: MutableList<Float> = mutableListOf()
            val rawAccelerationData = rawData.map { it.second }

            rawAccelerationData.forEach { data ->
                totAccResult.add(
                        sqrt(
                                data.first.pow(2)
                                        .plus(data.second.pow(2))
                                        .plus(data.third.pow(2))
                        )
                )
            }

            val signList = findOscillationSign(totAccResult, rawData)
            return totAccResult.mapIndexed { index, data -> data * signList[index] }
        }

        /**
         * Find the sign of the oscillation which gets lost in the total acceleration
         * calculation. Returns a list of signs.
         */
        private fun findOscillationSign(totalAccelerationAmplitude: List<Float>,
                                rawData: List<Pair<Float, Triple<Float, Float, Float>>>): List<Float> {
            // Separate raw sensor data into lists
            val timePoints = rawData.map { it.first }
            val rawX = rawData.map { it.second.first }
            val rawY = rawData.map { it.second.second }
            val rawZ = rawData.map { it.second.third }

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

            //Lists containing maxima points
            val tList: MutableList<Float> = mutableListOf()
            val xList: MutableList<Float> = mutableListOf()
            val yList: MutableList<Float> = mutableListOf()
            val zList: MutableList<Float> = mutableListOf()

            indexList.forEach { maximaIndex ->
                tList.add(totalAccelerationAmplitude[maximaIndex])
                xList.add(rawX[maximaIndex])
                yList.add(rawY[maximaIndex])
                zList.add(rawZ[maximaIndex])
            }

            /**
             * Main oscillation axis
             * The MOA is defined as the direction of the most recent maximum of the acceleration amplitude AA
             */
            val moaArray: Array<FloatArray> = Array(tList.size) {FloatArray(4) {0F} }
            moaArray[0][0] = timePoints[0]
            moaArray[0][1] = xList[0]
            moaArray[0][2] = yList[0]
            moaArray[0][3] = zList[0]

            for(i in 1 until tList.size) {
                if (((xList[i] * moaArray[i-1][1]) + (yList[i] * moaArray[i-1][2]) + (zList[i] * moaArray[i-1][3])) > 0) {

                    moaArray[i][0] = timePoints[i]
                    moaArray[i][1] = xList[i]
                    moaArray[i][2] = yList[i]
                    moaArray[i][3] = zList[i]
                } else {
                    moaArray[i][0] = timePoints[i]
                    moaArray[i][1] = -xList[i]
                    moaArray[i][2] = -yList[i]
                    moaArray[i][3] = -zList[i]
                }
            }

            /**
             * Find for each time step which MOA to use
             */
            val totList = mutableListOf<Int>()
            var indexLastMax = 1

            for (i in timePoints.indices) {
                if (indexLastMax < moaArray.size - 1) {
                    if (timePoints[i] == moaArray[indexLastMax + 1][0]) {
                        indexLastMax++
                    }
                }
                totList.add(indexLastMax)
            }

            /**
             * Determine the sign of the acceleration amplitude AA given by the MOA
             */
            val oscSign = mutableListOf<Float>()
            for (i in timePoints.indices) {
                val lastmax = totList[i]
                val sign = sign(rawX[i]*moaArray[lastmax][1] + rawY[i]*moaArray[lastmax][2] + rawZ[i]*moaArray[lastmax][3])
                oscSign.add(sign)
            }
            return oscSign
        }

    }


}