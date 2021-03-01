package com.krisgun.vibra.ui.collect_data

import android.app.Application
import android.content.Context
import android.hardware.*
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.*
import com.krisgun.vibra.data.Measurement
import com.krisgun.vibra.database.MeasurementRepository
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.*

private const val TAG = "CollectDataViewModel"

class CollectDataViewModel(application: Application) : AndroidViewModel(application),
        SensorEventListener {

    private lateinit var fileWriter: FileWriter
    private lateinit var file: File
    private lateinit var measurement: Measurement

    private var countLines = 0
    private var startTime = 0L
    private var endTime = 0L

    private val measurementRepository = MeasurementRepository.get()
    private val measurementIdLiveData = MutableLiveData<UUID>()

    val measurementLiveData: LiveData<Measurement?> =
            Transformations.switchMap(measurementIdLiveData) { id ->
                measurementRepository.getMeasurement(id)
            }

    private val _accelData = MutableLiveData<String>()
    val accelData: LiveData<String>
        get() = _accelData


    init {
        _accelData.value = "Initial value"
    }


    fun startCollectingData() {
        //Get measurement object from db

        //Start timer

        //Open file writer and register sensor
        Log.d(TAG, "Opening FileWriter with filename: ${measurement.rawDataFileName}")
        try {
            file = measurementRepository.getRawDataFile(measurement)
            fileWriter = FileWriter(file, true)
            if (file.length() == 0L) {
                Log.d(TAG, "Wrote CSV header.")
                fileWriter.write("Timestamp,X (m/s^2),Y (m/s^2),Z (m/s^2)")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        startTime = System.currentTimeMillis()
        registerSensor()
    }

    fun stopCollectingData() {
        endTime = System.currentTimeMillis()
        //Unregister listener and close filewriter
        try {
            fileWriter.close()
            val duration = (endTime - startTime) / 1000
            Log.d(TAG, "Closed FileWriter. Wrote $countLines lines in $duration seconds.")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        unregisterSensor()


        //Stop timer

        //Update duration if measurement is stopped before timer runs out
    }

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
            /*_accelData.postValue("timestamp: ${event.timestamp}\n " +
                    "x: ${event.values[0]}\n " +
                    "y: ${event.values[1]}\n " +
                    "z: ${event.values[2]}\n " +
                    "acc: ${event.accuracy}\n" +
                    "duration: ${measurement.duration}")*/
            fileWriter.write(String.format("%d, %f, %f, %f", event.timestamp, event.values[0], event.values[1], event.values[2]))
            countLines++
        } else {
            _accelData.postValue("No event")
        }
    }

    private fun registerSensor() {
        Log.d(TAG, "Registered Sensor Listener")
        sensorManager.let { sm ->
            sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER).let {
                sm.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME) //ca 50 per sekund med GAME
            }
        }
    }

    private fun unregisterSensor() {
        Log.d(TAG, "Unregistered Sensor Listener")
        sensorManager.unregisterListener(this)
    }

    fun setMeasurementId(id: UUID) {
        measurementIdLiveData.value = id
    }

    fun setMeasurement(measurement: Measurement) {
        this.measurement = measurement
    }
}