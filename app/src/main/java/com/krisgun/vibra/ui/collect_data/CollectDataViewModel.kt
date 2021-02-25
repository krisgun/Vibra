package com.krisgun.vibra.ui.collect_data

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

private const val TAG = "CollectDataViewModel"

class CollectDataViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {

    private val _accelData = MutableLiveData<String>()
    val accelData: LiveData<String>
        get() = _accelData



    init {
        _accelData.value = "Initial value"
    }

    private val sensorManager
        get() = getApplication<Application>().getSystemService(Context.SENSOR_SERVICE) as SensorManager

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //nothing
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            _accelData.postValue("timestamp: ${event.timestamp}\n x: ${event.values[0]}\n y: ${event.values[1]}\n z: ${event.values[2]}\n acc:  ${event.accuracy}")
        } else {
            _accelData.postValue("No event")
        }
    }

    fun registerSensor() {
        Log.d(TAG, "Registered Sensor Listener")
        sensorManager.let { sm ->
            sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER).let {
                sm.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
    }

    fun unregisterSensor() {
        Log.d(TAG, "Unregistered Sensor Listener")
        sensorManager.unregisterListener(this)
    }


}