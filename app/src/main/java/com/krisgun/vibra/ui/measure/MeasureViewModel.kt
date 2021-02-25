package com.krisgun.vibra.ui.measure

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.krisgun.vibra.R

private const val TAG = "MeasureViewModel"

class MeasureViewModel : ViewModel() {

    private lateinit var navController: NavController

    private val _durationMinutes = MutableLiveData<Int>()
    val durationMinutes: LiveData<Int>
        get() = _durationMinutes

    private val _durationSeconds = MutableLiveData<Int>()
    val durationSeconds: LiveData<Int>
        get() = _durationMinutes

    fun setNavController(navController: NavController) {
        this.navController = navController
    }

    fun onStart() {
        Log.d(TAG, "Clicky!")
        val action = MeasureFragmentDirections.actionNavigationMeasureToNavigationCollectData()
        navController.navigate(action)
    }

    fun onTextChangedDurationMinuteText() {
        Log.d(TAG, "Clicky duration minute text!")
    }
}