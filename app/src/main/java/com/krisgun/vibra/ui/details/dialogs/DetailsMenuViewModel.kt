package com.krisgun.vibra.ui.details.dialogs

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.krisgun.vibra.data.Measurement
import com.krisgun.vibra.database.MeasurementRepository
import com.krisgun.vibra.util.ObservableViewModel
import com.krisgun.vibra.BR
import com.krisgun.vibra.util.DataNames
import com.krisgun.vibra.util.Event
import java.io.File
import java.util.UUID

private const val TAG = "DetailsMenu"

class DetailsMenuViewModel : ObservableViewModel()  {

    /**
     * Measurement data
     */
    private val measurementRepository = MeasurementRepository.get()
    private val measurementIdLiveData = MutableLiveData<UUID>()
    val measurementsData: LiveData<List<Measurement>> = measurementRepository.getMeasurements()
    lateinit var measurement: Measurement
    lateinit var measurementsList: List<Measurement>

    val measurementLiveData: LiveData<Measurement?> =
            Transformations.switchMap(measurementIdLiveData) { id ->
                measurementRepository.getMeasurement(id)
            }

    private val _statusMessage = MutableLiveData<Event<String>>()
    val statusMessage: LiveData<Event<String>>
        get() = _statusMessage

    fun setMeasurementId(id: UUID) {
        measurementIdLiveData.value = id
    }

    fun deleteMeasurement() {
        measurementLiveData.value?.let { measurementRepository.deleteMeasurement(it) }
    }

    /**
     * Rename measurement
     */
    var titleLength = 128
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

    fun renameMeasurement(newTitle: String = titleText): Boolean {
        val oldMeasurement = this.measurement
        val newMeasurement = oldMeasurement.copy().apply {
            title = newTitle.trim()
        }

        if (newMeasurement.title.length > titleLength) {
            _statusMessage.value = Event("Title exceeds character limit.")
            return false
        }

        measurementsList.forEach {
            if (it.title == newMeasurement.title) {
                _statusMessage.value = Event("A measurement with the same title already exists.")
                return false
            }
        }
        measurementRepository.renameDataFiles(oldMeasurement, newMeasurement).also {
            measurementRepository.updateMeasurement(newMeasurement)
        }
        return true
    }


    /**
     * Share measurement
     */

    var isShareButtonEnabled = ObservableBoolean()
    fun handleShareButton() {
        var anyBoxTrue = false
        for (checkBox in checkBoxBooleans) {
            if (checkBox.get()) anyBoxTrue = true
        }
        if (anyBoxTrue) isShareButtonEnabled.set(true) else isShareButtonEnabled.set(false)
    }

    val checkBoxBooleans = Array<ObservableBoolean>(DataNames.values().size) {
        ObservableBoolean(true).apply {
            addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                    handleShareButton()
                }
            })
        }
    }

    fun setCheckBoxPreferences(preferences: List<Boolean>) {
        checkBoxBooleans.forEachIndexed { index, observableBoolean ->
            observableBoolean.set(preferences[index])
        }
    }

    fun getDataFiles(): List<File> {
        return measurementRepository.getAllFilesList(measurement).filterIndexed { index, _ ->
            checkBoxBooleans[index].get()
        }
    }

}