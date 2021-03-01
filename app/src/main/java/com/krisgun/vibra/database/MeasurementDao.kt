package com.krisgun.vibra.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.krisgun.vibra.data.Measurement

@Dao
interface MeasurementDao {

    @Query("SELECT * FROM measurement")
    fun getMeasurements(): LiveData<List<Measurement>>
}