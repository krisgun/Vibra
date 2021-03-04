package com.krisgun.vibra.ui.collect_data.dialog

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.krisgun.vibra.data.Measurement
import com.krisgun.vibra.database.MeasurementRepository
import java.util.*

private const val TAG = "StopMeasurementVM"

class StopMeasurementViewModel : ViewModel() {

    private lateinit var navController: NavController
    private val repository =  MeasurementRepository.get()
    private val measurementIdLiveData = MutableLiveData<UUID>()

    val measurementLiveData: LiveData<Measurement?> =
            Transformations.switchMap(measurementIdLiveData) { id ->
                repository.getMeasurement(id).also {
                    Log.d(TAG, "Got data")
                }
            }

    fun setMeasurementIdAndNavController(id: UUID, navController: NavController) {
        Log.d(TAG, "Got ID: $id")
        measurementIdLiveData.value = id
        this.navController = navController
    }

    fun onDiscard() {
        Log.d(TAG, "Discard pressed.")
        val measureAction = StopMeasurementDialogDirections
                .actionStopMeasurementDialogToNavigationMeasure()
        measurementLiveData.value?.let { repository.deleteMeasurement(it) }
        navController.navigate(measureAction)
    }

    fun onSave() {
        Log.d(TAG, "Save pressed.")
        val detailAction = measurementIdLiveData.value?.let {
            StopMeasurementDialogDirections
                .actionStopMeasurementDialogToNavigationDetails(it)
        }
        if (detailAction != null) {
            navController.navigate(detailAction)
        }
    }
}