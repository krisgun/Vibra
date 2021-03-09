package com.krisgun.vibra.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Measurement(@PrimaryKey val id: UUID = UUID.randomUUID(),
                        var title: String = "Measurement ${id.toString().subSequence(0..7)}",
                        var date: Date = Date(),
                        var duration_seconds: Int,
                        var sampling_frequency: Int) {

    val rawDataFileName
        get() = "SENSOR_$date.csv"
}