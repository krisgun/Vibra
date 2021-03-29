package com.krisgun.vibra.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Measurement(@PrimaryKey val id: UUID = UUID.randomUUID(),
                        var title: String = "Measurement ${id.toString().subSequence(0..7)}",
                        var date: Date = Date(),
                        var duration_seconds: Int,
                        var sampling_frequency: Double,
                        var num_of_datapoints: Int = 0) {

    val rawAccFileName
        get() = "${title}_RAW_ACC_SENSOR.csv"

    val rawGyroFileName
        get() = "${title}_RAW_GYRO_SENSOR.csv"

    val totalAccelerationFileName
        get() = "${title}_TOTAL_ACC.csv"

    val amplitudeSpectrumFileName
        get() = "${title}_AMP_SPECTRUM.csv"

    val powerSpectrumFileName
        get() = "${title}_POW_SPECTRUM.csv"
}