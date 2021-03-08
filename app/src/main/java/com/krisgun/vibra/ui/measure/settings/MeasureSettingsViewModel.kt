package com.krisgun.vibra.ui.measure.settings

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.util.Log
import androidx.databinding.*
import androidx.lifecycle.AndroidViewModel
import com.krisgun.vibra.BR
import com.krisgun.vibra.R
import kotlin.math.roundToInt

private const val TAG = "MeasureSettingViewModel"
private const val SHARED_PREFS_KEY = "measurement"

class MeasureSettingsViewModel(application: Application) : AndroidViewModel(application), Observable {

    private val application_instance = getApplication<Application>()

    private val sensorManager
        get() = application_instance.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val minAccelerometerDelay = sensorManager
        .getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        .minDelay
        .toFloat()
        .div(1000000F) //From microseconds to seconds

    val maxFrequency = (1.0F / minAccelerometerDelay).roundToInt().toFloat()
    val minFrequency = 10.0F

    /**
     * Two-way data binding
     */
    @get:Bindable
    var samplingFrequency: Float = 10F
    set(value) {
        if (value > minFrequency) {
            field = value
            notifyPropertyChanged(BR.samplingFrequency)
            val sharedPrefs =
                application_instance.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE)
            sharedPrefs.edit().apply {
                putFloat(application_instance.getString(R.string.prefs_sampling_frequency), value)
                apply()
            }
        }
    }
    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }
    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.remove(callback)
    }
    fun notifyPropertyChanged(fieldId: Int) {
        callbacks.notifyCallbacks(this, fieldId, null)
    }

    /**
     * Shared Preferences
     */
    fun restorePreferences() {
        val sharedPrefs =
            application_instance.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE) ?: return
        val samplingFrequencyKey = application_instance.getString(R.string.prefs_sampling_frequency)
        if (sharedPrefs.contains(samplingFrequencyKey)) {
            samplingFrequency = sharedPrefs.getFloat(samplingFrequencyKey, 50.0F)
        }
    }



}