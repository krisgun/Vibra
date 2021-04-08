package com.krisgun.vibra.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.krisgun.vibra.data.Measurement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
            getRawAccDataFile(oldMeasurement).renameTo(File(filesDir, newMeasurement.rawAccFileName))
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
            getRawAccDataFile(measurement).delete()
            getRawGyroDataFile(measurement).let { if (it.exists()) it.delete() }
            getTotalAccelerationFile(measurement).let { if (it.exists()) it.delete() }
            getPowerSpectrumFile(measurement).let { if (it.exists()) it.delete() }
            getAmplitudeSpectrumFile(measurement).let { if (it.exists()) it.delete() }

            measurementDao.deleteMeasurement(measurement)
        }
    }

    fun getAllFilesList(measurement: Measurement): List<File> {
        val filesList = mutableListOf<File>()
        filesList.apply {
            add(getRawAccDataFile(measurement))
            add(getRawGyroDataFile(measurement))
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
    fun getRawAccDataFile(measurement: Measurement): File = File(filesDir, measurement.rawAccFileName)
    fun getRawGyroDataFile(measurement: Measurement): File = File(filesDir, measurement.rawGyroFileName)

    suspend fun getTotalAccelerationFromFile(measurement: Measurement): List<Pair<Long, Float>> {
        val file: File = getTotalAccelerationFile(measurement)
        val totalAccelerationPairList = mutableListOf<Pair<Long, Float>>()

        withContext(Dispatchers.IO) {
            try {
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
            } catch (cause: Throwable) {
                throw ReadDataFromFileException("Could not get total acceleration from file: ${file.absolutePath}", cause)
            }
        }
        return totalAccelerationPairList
    }

    suspend fun getAmplitudeSpectrumFromFile(measurement: Measurement): List<Pair<Double, Double>> {
        val file = getAmplitudeSpectrumFile(measurement)
        val amplitudeSpectrumPairList = mutableListOf<Pair<Double, Double>>()
        withContext(Dispatchers.IO) {
            try {
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
            } catch (cause: Throwable) {
                throw ReadDataFromFileException("Could not get amplitude spectrum from file: ${file.absolutePath}", cause)
            }
        }
        return amplitudeSpectrumPairList
    }

    suspend fun getPowerSpectrumFromFile(measurement: Measurement): List<Pair<Double, Double>> {
        val file = getPowerSpectrumFile(measurement)
        val powerSpectrumPairList = mutableListOf<Pair<Double, Double>>()

        withContext(Dispatchers.IO) {
            try {
                file.forEachLine { eachLine ->
                    if (!eachLine.contains("Power")) {
                        eachLine.split(",").also { splitList ->
                            powerSpectrumPairList.add(
                                    Pair(
                                            splitList[0].toDouble(),
                                            (10.0).pow((splitList[1].toDouble()).div(10))
                                    )
                            )
                        }
                    }
                }
            } catch (cause: Throwable) {
                throw ReadDataFromFileException("Could not get power spectrum from file: ${file.absolutePath}", cause)
            }
        }
        return powerSpectrumPairList
    }

    suspend fun writeTotalAccelerationToFile(measurement: Measurement, data: List<Pair<Long, Float>>) {
        val file = getTotalAccelerationFile(measurement)
        withContext(Dispatchers.IO) {
            try {
                val fileWriter = FileWriter(file, true)
                fileWriter.write("Timestamp (ns),Total Acceleration (m/s^2)\n")
                data.forEach {
                    fileWriter.write(String.format(Locale.US, "%d,%f\n", it.first, it.second))
                }
                fileWriter.close()
            } catch (cause: Throwable) {
                throw WriteDataToFileException("Could not write total acceleration to file: ${file.absolutePath}", cause)
            }
        }
    }

    suspend fun writeAmplitudeSpectrumToFile(measurement: Measurement, data: List<Pair<Double, Double>>) {
        val file = getAmplitudeSpectrumFile(measurement)
        withContext(Dispatchers.IO) {
            try {
                val fileWriter = FileWriter(file, true)
                fileWriter.write("Frequency (Hz),Amplitude (|P1(f)|)\n")
                data.forEach {
                    fileWriter.write(String.format(Locale.US, "%f,%f\n", it.first, it.second))
                }
                fileWriter.close()
            } catch (cause: Throwable) {
                throw WriteDataToFileException("Could not write amplitude spectrum to file: ${file.absolutePath}", cause)
            }
        }
    }

   suspend fun writePowerSpectrumToFile(measurement: Measurement, data: List<Pair<Double, Double>>) {
       val file = getPowerSpectrumFile(measurement)
       withContext(Dispatchers.IO) {
           try {
               val fileWriter = FileWriter(file, true)
               fileWriter.write("Frequency (Hz),Power (dB)\n")
               data.forEach {
                   fileWriter.write(String.format(Locale.US, "%f,%f\n", it.first, 10 * log10(it.second)))
               }
               fileWriter.close()
           } catch (cause: Throwable) {
               throw WriteDataToFileException("Could not write power spectrum to file: ${file.absolutePath}", cause)
           }
        }
    }

    /**
     * Returns a List of Pairs in the form of: (timestamp, (xData, yData, zData))
     */
    suspend fun getRawAccDataFromFile(measurement: Measurement): List<Pair<Long, Triple<Float, Float, Float>>> {
        val file: File = getRawAccDataFile(measurement)
        val rawDataTripleList: MutableList<Pair<Long, Triple<Float, Float, Float>>> = mutableListOf()

        withContext(Dispatchers.IO) {
            try {
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
            } catch (cause: Throwable) {
                throw ReadDataFromFileException("Could not get accelerometer data from file: ${file.absolutePath}", cause)
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

    class ReadDataFromFileException(message: String, cause: Throwable) : Throwable(message, cause)
    class WriteDataToFileException(message: String, cause: Throwable) : Throwable(message, cause)
}