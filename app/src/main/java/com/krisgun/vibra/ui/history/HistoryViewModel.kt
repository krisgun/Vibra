package com.krisgun.vibra.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.krisgun.vibra.data.Measurement
import com.krisgun.vibra.database.MeasurementRepository

class HistoryViewModel : ViewModel() {


    private val measurementRepository = MeasurementRepository.get()
    val measurementsData: LiveData<List<Measurement>>  = measurementRepository.getMeasurements()

    var listComparator: Comparator<Measurement>

    init {
        listComparator = compareBy { it.title }
    }

    fun setComparatorToTitleDescending() {
        listComparator = compareBy { it.title }
    }

    fun setComparatorToDateAscending() {
        listComparator = compareBy { it.date }
    }
}


