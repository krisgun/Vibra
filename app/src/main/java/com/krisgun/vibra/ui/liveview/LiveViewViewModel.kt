package com.krisgun.vibra.ui.liveview

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.HandlerThread
import android.os.Process
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.apache.commons.math3.stat.Frequency
import java.lang.ArithmeticException
import kotlin.math.roundToInt

private const val TAG = "LiveView"

class LiveViewViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {

    //Sensor thread handlers
    private lateinit var mSensorThread: HandlerThread
    private lateinit var mSensorHandler: Handler
    var samplingFrequency = 50

    private var timestamps = mutableListOf<Long>()
    private var printFrequency: Int = 1

    //UI Data
    private val _accelSensorData = MutableLiveData<FloatArray>()
    val accelSensorData: LiveData<FloatArray>
        get() = _accelSensorData

    private val _gyroSensorData = MutableLiveData<FloatArray>()
    val gyroSensorData: LiveData<FloatArray>
        get() = _gyroSensorData

    private val _measuredFrequencyData = MutableLiveData<Double>()
    val measuredFrequencyData: LiveData<Double>
        get() = _measuredFrequencyData

    /**
     * Sensor handling
     */
    private val sensorManager
        get() = getApplication<Application>().getSystemService(Context.SENSOR_SERVICE) as SensorManager

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //nothing
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            when(event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    timestamps.add(event.timestamp)
                    if (timestamps.size % printFrequency == 0) {
                        _accelSensorData.postValue(event.values)
                        if (timestamps.size >= 25) {
                            _measuredFrequencyData.postValue(
                                    timestamps.size.div(
                                            ((timestamps.last() - timestamps.first()) / 1000000000.0)
                                    )
                            )
                        }

                    }
                }
                Sensor.TYPE_GYROSCOPE -> {
                    if (timestamps.size % printFrequency == 0) {
                        _gyroSensorData.postValue(event.values)
                    }
                }
                else -> Log.d(TAG, "Unexpected sensor type: ${event.sensor.type}")
            }
        }
    }

    fun registerSensor() {
        mSensorThread = HandlerThread("Sensor Thread", Process.THREAD_PRIORITY_MORE_FAVORABLE)
        mSensorThread.start()
        mSensorHandler = Handler(mSensorThread.looper)

        sensorManager.let { sm ->

            val sensorDelay = ((1F / samplingFrequency) * 1000000F).roundToInt()

            sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER).let {
                sm.registerListener(this, it, sensorDelay, mSensorHandler)
            }
            sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE).let {
                sm.registerListener(this, it, sensorDelay, mSensorHandler)
            }
        }
        printFrequency = if (samplingFrequency / 4 == 0) {
            2
        } else {
            samplingFrequency / 4
        }
        timestamps.clear()
    }

    fun unregisterSensor() {
        sensorManager.unregisterListener(this)
        mSensorThread.quitSafely()
    }
}
