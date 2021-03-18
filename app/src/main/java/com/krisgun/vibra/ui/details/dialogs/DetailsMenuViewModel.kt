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
import java.util.UUID

private const val TAG = "DetailsMenu"

class DetailsMenuViewModel : ObservableViewModel()  {

    private val measurementRepository = MeasurementRepository.get()
    private val measurementIdLiveData = MutableLiveData<UUID>()
    lateinit var measurement: Measurement

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

    fun renameMeasurement(newTitle: String = titleText) {
        val oldMeasurement = this.measurement
        val newMeasurement = oldMeasurement.copy().apply {
            title = newTitle
        }
        /**
         * TODO: Check for duplicate file-names
         */
        //See if it logs correctly before applying changes
        //Log.d(TAG, "oldTitle: ${oldMeasurement.title}\tnewTitle: ${newMeasurement.title}")
        measurementRepository.renameDataFiles(oldMeasurement, newMeasurement)
        measurementRepository.updateMeasurement(newMeasurement)
    }
}