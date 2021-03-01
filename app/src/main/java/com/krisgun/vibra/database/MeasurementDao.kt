package com.krisgun.vibra.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.krisgun.vibra.data.Measurement
import java.util.*

@Dao
interface MeasurementDao {

    //Gets all measurements
    @Query("SELECT * FROM measurement")
    fun getMeasurements(): LiveData<List<Measurement>>

    //Gets measurement with a certain id
    @Query("SELECT * FROM measurement WHERE id= :id")
    fun getMeasurement(id: UUID): LiveData<Measurement?>

    @Update
    fun updateMeasurement(measurement: Measurement)

    @Insert
    fun addMeasurement(measurement: Measurement)

    @Delete
    fun deleteMeasurement(measurement: Measurement)


}