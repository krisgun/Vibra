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

    //Simple test gave 4ms on 30k lines
    fun getRawData2DArray(measurement: Measurement): Array<FloatArray> {
        val file: File = getRawDataFile(measurement)

        val timeMutableList: MutableList<Float> = mutableListOf()
        val xMutableList: MutableList<Float> = mutableListOf()
        val yMutableList: MutableList<Float> = mutableListOf()
        val zMutableList: MutableList<Float> = mutableListOf()

        file.forEachLine { eachLine ->
            if (!eachLine.contains("Timestamp")) { //Check if title row

                eachLine.split(",").also { splitList ->
                    timeMutableList.add(splitList[0].toFloat())
                    xMutableList.add(splitList[1].toFloat())
                    yMutableList.add(splitList[2].toFloat())
                    zMutableList.add(splitList[3].toFloat())
                }
            }
        }

        val timeFloatArray: FloatArray = timeMutableList.toFloatArray()
        val xFloatArray: FloatArray = xMutableList.toFloatArray()
        val yFloatArray: FloatArray = yMutableList.toFloatArray()
        val zFloatArray: FloatArray = zMutableList.toFloatArray()

        return arrayOf(timeFloatArray, xFloatArray, yFloatArray, zFloatArray)
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