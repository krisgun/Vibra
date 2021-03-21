package com.krisgun.vibra.database

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.krisgun.vibra.data.Measurement
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.*
import java.util.concurrent.Executors
import kotlin.math.log10
import kotlin.math.pow

private const val DATABASE_NAME = "measurement-database"
private const val TAG = "MeasurementRepository"

class MeasurementRepository private constructor(context: Context){

    private val database: MeasurementDatabase = Room.databaseBuilder(
        context.applicationContext,
        MeasurementDatabase::class.java,
        DATABASE_NAME)
            .build()

    private val measurementDao = database.measurementDao()
    private val executor = Executors.newSingleThreadExecutor()
    private val filesDir = context.applicationContext.getExternalFilesDir(null)?.absolutePath

    fun getMeasurements(): LiveData<List<Measurement>> = measurementDao.getMeasurements()

    fun getMeasurement(id: UUID): LiveData<Measurement?> = measurementDao.getMeasurement(id)

    fun updateMeasurement(measurement: Measurement) {
        executor.execute {
            measurementDao.updateMeasurement(measurement)
        }
    }

    fun renameDataFiles(oldMeasurement: Measurement, newMeasurement: Measurement) {
        executor.execute {
            getRawDataFile(oldMeasurement).renameTo(File(filesDir, newMeasurement.rawDataFileName))
            getTotalAccelerationFile(oldMeasurement).let {
                if (it.exists()) {
                    it.renameTo(File(filesDir, newMeasurement.totalAccelerationFileName))
                }
            }
            getPowerSpectrumFile(oldMeasurement).let {
                if (it.exists()) {
                    it.renameTo(File(filesDir, newMeasurement.powerSpectrumFileName))
                }
            }
            getAmplitudeSpectrumFile(oldMeasurement).let {
                if (it.exists()) {
                    it.renameTo(File(filesDir, newMeasurement.amplitudeSpectrumFileName))
                }
            }
        }
    }

    fun addMeasurement(measurement: Measurement) {
        executor.execute {
            measurementDao.addMeasurement(measurement)
        }
    }

    fun deleteMeasurement(measurement: Measurement) {
        executor.execute {

            //Delete data-files
            getRawDataFile(measurement).delete()
            getTotalAccelerationFile(measurement).let { if (it.exists()) it.delete() }
            getPowerSpectrumFile(measurement).let { if (it.exists()) it.delete() }
            getAmplitudeSpectrumFile(measurement).let { if (it.exists()) it.delete() }

            measurementDao.deleteMeasurement(measurement)
        }
    }

    fun getAllFilesList(measurement: Measurement): List<File> {
        val filesList = mutableListOf<File>()
        filesList.apply {
            add(getRawDataFile(measurement))
            add(getTotalAccelerationFile(measurement))
            add(getAmplitudeSpectrumFile(measurement))
            add(getPowerSpectrumFile(measurement))
        }.also {
            return it
        }
    }

    fun getTotalAccelerationFile(measurement: Measurement): File = File(filesDir, measurement.totalAccelerationFileName)
    fun getAmplitudeSpectrumFile(measurement: Measurement): File = File(filesDir, measurement.amplitudeSpectrumFileName)
    fun getPowerSpectrumFile(measurement: Measurement): File = File(filesDir, measurement.powerSpectrumFileName)
    fun getRawDataFile(measurement: Measurement): File = File(filesDir, measurement.rawDataFileName)

    fun getTotalAccelerationFromFile(measurement: Measurement): List<Pair<Long, Float>> {
        if (filesDir != null) {
            Log.d(TAG, filesDir)
        }
        val file: File = getTotalAccelerationFile(measurement)
        val totalAccelerationPairList = mutableListOf<Pair<Long, Float>>()

        file.forEachLine { eachLine ->
            if (!eachLine.contains("Timestamp")) {
                eachLine.split(",").also { splitList ->
                    totalAccelerationPairList.add(
                            Pair(
                                    splitList[0].toLong(),
                                    splitList[1].toFloat())
                    )
                }
            }
        }
        return totalAccelerationPairList
    }

    fun getAmplitudeSpectrumFromFile(measurement: Measurement): List<Pair<Double, Double>> {
        val file = getAmplitudeSpectrumFile(measurement)
        val amplitudeSpectrumPairList = mutableListOf<Pair<Double, Double>>()

        file.forEachLine { eachLine ->
            if (!eachLine.contains("Frequency")) {
                eachLine.split(",").also { splitList ->
                    amplitudeSpectrumPairList.add(
                            Pair(
                                    splitList[0].toDouble(),
                                    splitList[1].toDouble()
                            )
                    )
                }
            }
        }
        return amplitudeSpectrumPairList
    }

    fun getPowerSpectrumFromFile(measurement: Measurement): List<Pair<Double, Double>> {
        val file = getPowerSpectrumFile(measurement)
        val powerSpectrumPairList = mutableListOf<Pair<Double, Double>>()

        file.forEachLine { eachLine ->
            if (!eachLine.contains("Power")) {
                eachLine.split(",").also { splitList ->
                    powerSpectrumPairList.add(
                            Pair(
                                    splitList[0].toDouble(),
                                    10.0.pow((splitList[1].toDouble()).div(10))
                            )
                    )
                }
            }
        }
        return powerSpectrumPairList
    }

    fun writeTotalAccelerationToFile(measurement: Measurement, data: List<Pair<Long, Float>>) {
        executor.execute {
            val file = getTotalAccelerationFile(measurement)
            try {
                val fileWriter = FileWriter(file, true)
                fileWriter.write("Timestamp (ns),Total Acceleration (m/s^2)\n")
                data.forEach {
                    fileWriter.write(String.format(Locale.US, "%d,%f\n", it.first, it.second))
                }
                fileWriter.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun writeAmplitudeSpectrumToFile(measurement: Measurement, data: List<Pair<Double, Double>>) {
        executor.execute {
            val file = getAmplitudeSpectrumFile(measurement)
            try {
                val fileWriter = FileWriter(file, true)
                fileWriter.write("Frequency (Hz),Amplitude (|P1(f)|)\n")
                data.forEach {
                    fileWriter.write(String.format(Locale.US, "%f,%f\n", it.first, it.second))
                }
                fileWriter.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun writePowerSpectrumToFile(measurement: Measurement, data: List<Pair<Double, Double>>) {
        executor.execute {
            val file = getPowerSpectrumFile(measurement)
            try {
                val fileWriter = FileWriter(file, true)
                fileWriter.write("Frequency (Hz),Power (dB)\n")
                data.forEach {
                    fileWriter.write(String.format(Locale.US, "%f,%f\n", it.first, 10 * log10(it.second)))
                }
                fileWriter.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Returns a List of Pairs in the form of: (timestamp, (xData, yData, zData))
     */
    fun getRawDataFromFile(measurement: Measurement): List<Pair<Long, Triple<Float, Float, Float>>> {
        val file: File = getRawDataFile(measurement)

        val rawDataTripleList: MutableList<Pair<Long, Triple<Float, Float, Float>>> = mutableListOf()

        file.forEachLine { eachLine ->
            if (!eachLine.contains("Timestamp")) { //Check if title row

                eachLine.split(",").also { splitList ->
                    rawDataTripleList.add(
                            Pair(
                                    splitList[0].toLong(),
                                    Triple(
                                            splitList[1].toFloat(),
                                            splitList[2].toFloat(),
                                            splitList[3].toFloat()
                                    )
                            )
                    )
                }
            }
        }
        return rawDataTripleList
    }

    /**
     * TODO:  ADD MORE DATABASE FUNCTIONS
     */

    companion object {
        private var INSTANCE: MeasurementRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = MeasurementRepository(context)
            }
        }

        fun get(): MeasurementRepository {
            return INSTANCE ?:
            throw IllegalStateException("MeasurementRepository must be initialized.")
        }
    }
}