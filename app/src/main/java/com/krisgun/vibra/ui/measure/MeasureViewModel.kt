package com.krisgun.vibra.ui.measure

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.krisgun.vibra.R
import com.krisgun.vibra.util.ObservableViewModel

private const val TAG = "MeasureViewModel"

class MeasureViewModel : ObservableViewModel() {

    private lateinit var navController: NavController

    private val _durationMinutes = MutableLiveData<String>()
    val durationMinutes: LiveData<String>
        get() = _durationMinutes

    private val _durationSeconds = MutableLiveData<String>()
    val durationSeconds: LiveData<String>
        get() = _durationSeconds

    private val _countdownSeconds = MutableLiveData<String>()
    val countdownSeconds: LiveData<String>
        get() = _countdownSeconds

    init {
        _durationMinutes.value = "01"
        _durationSeconds.value = "30"
        _countdownSeconds.value = "05"
    }

    fun setNavController(navController: NavController) {
        this.navController = navController
    }

    fun onStart() {
        Log.d(TAG, "Clicky!")
        val action = MeasureFragmentDirections.actionNavigationMeasureToNavigationCollectData()
        navController.navigate(action)
    }

    /**
     * TODO: IMPLEMENT AFTER TEXT CHANGED. SINGLE DIGIT SHOULD BE PREPENDED WITH A 0. EX 1 -> 01
     */

    fun onTextChangedDurationMinuteText(text: CharSequence) {
        onTextChangedSetTimeText(text, _durationMinutes)
    }

    fun onTextChangedDurationSecondsText(text: CharSequence) {
        onTextChangedSetTimeText(text, _durationSeconds)
    }

    fun onTextChangedCountdownSecondsText(text: CharSequence) {
        onTextChangedSetTimeText(text, _countdownSeconds)
    }

    private fun onTextChangedSetTimeText(text: CharSequence, data: MutableLiveData<String>) {
        if (text.isNotEmpty()) {
            val textToChange = text.toString().toInt()
            if (textToChange in 6..9) {
                textToChange.toString()
                data.value = "0$textToChange"
            }
        }
    }
}