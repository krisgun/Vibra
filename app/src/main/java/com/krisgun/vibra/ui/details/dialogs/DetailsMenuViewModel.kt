package com.krisgun.vibra.ui.details.dialogs

import android.util.Log
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.krisgun.vibra.data.Measurement
import com.krisgun.vibra.database.MeasurementRepository
import com.krisgun.vibra.util.ObservableViewModel
import com.krisgun.vibra.BR
import java.io.File
import java.util.UUID

private const val TAG = "DetailsMenu"

class DetailsMenuViewModel : ObservableViewModel()  {

    val measurementRepository = MeasurementRepository.get()
    private val measurementIdLiveData = MutableLiveData<UUID>()
    val measurementsData: LiveData<List<Measurement>> = measurementRepository.getMeasurements()
    lateinit var measurement: Measurement
    lateinit var measurementsList: List<Measurement>

    var isRenameButtonEnabled = ObservableBoolean()
    @get:Bindable
    var titleText: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.titleText)
            if (value.isNotEmpty())
                isRenameButtonEnabled.set(true)
            else
                isRenameButtonEnabled.set(false)
        }

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

    fun getRawDataFile(): File {
        return measurementRepository.getRawDataFile(measurement)
    }

    fun renameMeasurement(newTitle: String = titleText): Boolean {
        val oldMeasurement = this.measurement
        val newMeasurement = oldMeasurement.copy().apply {
            title = newTitle
        }

        measurementsList.forEach {
            if (it.title == newTitle) {
                return false
            }
        }
        measurementRepository.renameDataFiles(oldMeasurement, newMeasurement).also {
            measurementRepository.updateMeasurement(newMeasurement)
        }
        return true
    }

}