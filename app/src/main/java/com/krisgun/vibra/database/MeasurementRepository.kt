package com.krisgun.vibra.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.room.Room
import com.krisgun.vibra.data.Measurement
import java.io.File
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "measurement-database"

class MeasurementRepository private constructor(context: Context){

    private val database: MeasurementDatabase = Room.databaseBuilder(
        context.applicationContext,
        MeasurementDatabase::class.java,
        DATABASE_NAME).build()

    private val measurementDao = database.measurementDao()
    private val executor = Executors.newSingleThreadExecutor()
    private val filesDir = context.applicationContext.filesDir

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
            measurementDao.deleteMeasurement(measurement)
        }
    }

    fun getRawDataFile(measurement: Measurement): File = File(filesDir, measurement.rawDataFileName)

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