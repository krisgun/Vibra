package com.krisgun.vibra.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.krisgun.vibra.data.Measurement
import java.io.File
import java.util.*
import java.util.concurrent.Executors

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

    fun addMeasurement(measurement: Measurement) {
        executor.execute {
            measurementDao.addMeasurement(measurement)
        }
    }

    fun deleteMeasurement(measurement: Measurement) {
        executor.execute {
            val rawFile = getRawDataFile(measurement)
            rawFile.delete()
            measurementDao.deleteMeasurement(measurement)
        }
    }

    fun getRawDataFile(measurement: Measurement): File = File(filesDir, measurement.rawDataFileName)

    /**
     * Returns a Pair with (timestamp, (xData, yData, zData))
     */
    fun getRawDataTuple(measurement: Measurement): List<Pair<Float, Triple<Float, Float, Float>>> {
        val file: File = getRawDataFile(measurement)

        val rawDataTripleList: MutableList<Pair<Float, Triple<Float, Float, Float>>> = mutableListOf()

        file.forEachLine { eachLine ->
            if (!eachLine.contains("Timestamp")) { //Check if title row

                eachLine.split(",").also { splitList ->
                    rawDataTripleList.add(
                            Pair(
                                    splitList[0].toFloat(),
                            Triple(
                                    splitList[1].toFloat(),
                                    splitList[2].toFloat(),
                                    splitList[3].toFloat()
                            )
                    ))
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