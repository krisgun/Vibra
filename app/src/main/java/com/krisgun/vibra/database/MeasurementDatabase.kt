package com.krisgun.vibra.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.krisgun.vibra.data.Measurement

@Database(entities = [ Measurement::class ], version = 1)
@TypeConverters(MeasurementTypeConverters::class)
abstract class MeasurementDatabase : RoomDatabase() {

    abstract fun measurementDao(): MeasurementDao
}