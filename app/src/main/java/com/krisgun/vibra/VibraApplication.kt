package com.krisgun.vibra

import android.app.Application
import com.krisgun.vibra.database.MeasurementRepository

class VibraApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MeasurementRepository.initialize(this)
    }
}