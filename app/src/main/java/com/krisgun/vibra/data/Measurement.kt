package com.krisgun.vibra.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Measurement(@PrimaryKey val id: UUID = UUID.randomUUID(),
                        var title: String,
                        var date: Date = Date()) {

    val rawDataFileName
        get() = "SENSOR_$date.csv"
}