package com.krisgun.vibra.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.krisgun.vibra.data.Measurement
import com.krisgun.vibra.database.MeasurementRepository

class HistoryViewModel : ViewModel() {

    private val measurementRepository = MeasurementRepository.get()
    val measurementsData: LiveData<List<Measurement>> = measurementRepository.getMeasurements()
}


