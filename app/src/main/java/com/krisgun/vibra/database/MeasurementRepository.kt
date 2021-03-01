package com.krisgun.vibra.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.krisgun.vibra.data.Measurement
import java.io.File
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