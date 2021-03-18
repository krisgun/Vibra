package com.krisgun.vibra.ui.details.dialogs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.krisgun.vibra.data.Measurement
import com.krisgun.vibra.database.MeasurementRepository
import java.util.*

class DetailsMenuViewModel : ViewModel() {

    private val measurementRepository = MeasurementRepository.get()
    private val measurementIdLiveData = MutableLiveData<UUID>()

    val measurementLiveData: LiveData<Measurement?> =
            Transformations.switchMap(measurementIdLiveData) { id ->
                measurementRepository.getMeasurement(id)
            }

    fun setMeasurementId(id: UUID) {
        measurementIdLiveData.value = id
    }

    fun deleteMeasurement() {
        measurementLiveData.value?.let { measurementRepository.deleteMeasurement(it) }
    }
}