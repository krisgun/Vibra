package com.krisgun.vibra.ui.collect_data

import android.app.Application
import android.content.Context
import android.hardware.*
import android.os.CountDownTimer
import android.os.Handler
import android.os.HandlerThread
import android.os.Process
import android.util.Log
import androidx.lifecycle.*
import androidx.navigation.NavController
import com.krisgun.vibra.data.Measurement
import com.krisgun.vibra.database.MeasurementRepository
import com.krisgun.vibra.ui.collect_data.dialog.StopMeasurementDialogDirections
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.*
import kotlin.math.ceil
import kotlin.math.roundToInt

private const val TAG = "CollectData"

class CollectDataViewModel(application: Application) : AndroidViewModel(application),
        SensorEventListener {

    //Sensor thread handlers
    private lateinit var mSensorThread: HandlerThread
    private lateinit var mSensorHandler: Handler

    //File objects
    private lateinit var fileWriter: FileWriter
    private lateinit var file: File

    //Fetched measurement object from DB
    private lateinit var measurement: Measurement
    private lateinit var navController: NavController

    //Timer
    private lateinit var countDownTimer: CountDownTimer
    private var timerDuration = 0L
    var isTimerFinished = false
    private var actualDuration = 0

    //Progressbar
    var maxProgress = 100
    //Benchmarking
    private var countLines = 0
    private var startTime = 0L
    private var endTime = 0L

    private val measurementRepository = MeasurementRepository.get()
    private val measurementIdLiveData = MutableLiveData<UUID>()

    val measurementLiveData: LiveData<Measurement?> =
            Transformations.switchMap(measurementIdLiveData) { id ->
                measurementRepository.getMeasurement(id)
            }

    //UI data
    private val _durationData = MutableLiveData<Int>()
    val durationData: LiveData<Int>
        get() = _durationData

    private val _progressData = MutableLiveData<Int>()
    val progressData: LiveData<Int>
        get() = _progressData

    private val _sensorData = MutableLiveData<String>()
    val sensorData: LiveData<String>
        get() = _sensorData

    init {
        _sensorData.value = "No sensor data available."
        _progressData.value = 0
    }

    /**
     * Data Collection handling
     */
    fun startCollectingData() {

        //Open file writer and register sensor
        Log.d(TAG, "Opening FileWriter with filename: ${file.name}")
        try {
            fileWriter = FileWriter(file, true)
            if (file.length() == 0L) {
                Log.d(TAG, "Wrote CSV header.")
                fileWriter.write("Timestamp,X (m/s^2),Y (m/s^2),Z (m/s^2)\n")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        startTime = System.currentTimeMillis()
        registerSensor()
        startTimer(timerDuration)

    }

    fun stopCollectingData() {

        //Unregister listener and measure time
        unregisterSensor()
        endTime = System.currentTimeMillis()
        val duration = (endTime - startTime).also {
            if (actualDuration == 0) {
                actualDuration = (it / 1000.0).roundToInt()
            }
        }

        // Close filewriter
        try {
            fileWriter.close()
            Log.d(TAG, "Closed FileWriter. Wrote $countLines lines in $duration ms.")
        } catch (e: IOException) {
            e.printStackTrace()
        }

        countDownTimer.cancel()
    }

    /**
     * CountDownTimer handling
     */
    private fun startTimer(millisTimerDuration: Long) {
        countDownTimer = object : CountDownTimer(millisTimerDuration, 1000) {
            override fun onFinish() {
                _durationData.postValue(0)
                _progressData.postValue(maxProgress)
                isTimerFinished = true
                Log.d(TAG, "Timer finished.")
                stopCollectingData()

                //Update measurement sampling frequency
                measurement.sampling_frequency = countLines / actualDuration
                measurementRepository.updateMeasurement(measurement)

                //Navigate to detail view
                val action = CollectDataFragmentDirections
                        .actionNavigationCollectDataToNavigationDetails(measurement.id)
                navController.navigate(action)
            }

            override fun onTick(millisUntilFinished: Long) {
                _durationData.postValue(ceil((millisUntilFinished / 1000.0)).toInt())
                _progressData.postValue(100 - ((millisUntilFinished.toFloat() / timerDuration.toFloat())*100).toInt())
            }
        }
        countDownTimer.start()
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

            if(countLines % (measurement.sampling_frequency / 2) == 0) {
                _sensorData.postValue("x: ${event.values[0]}\n y: ${event.values[1]}\n z: ${event.values[2]}")
            }

            //Write sensor data to file
            fileWriter.write(String.format(Locale.US,"%d,%f,%f,%f\n", event.timestamp, event.values[0], event.values[1], event.values[2]))
            countLines++
        }
    }

    private fun registerSensor() {
        mSensorThread = HandlerThread("Sensor Thread", Process.THREAD_PRIORITY_MORE_FAVORABLE)
        mSensorThread.start()
        mSensorHandler = Handler(mSensorThread.looper)

        sensorManager.let { sm ->

            val sensorDelay = ((1F / measurement.sampling_frequency) * 1000000F).roundToInt()

            Log.d(TAG, "Accelerometer list: ${sm.getSensorList(Sensor.TYPE_ACCELEROMETER)}, currDelay: $sensorDelay")

            sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER).let {
                sm.registerListener(this, it, sensorDelay, mSensorHandler) //ca 50 per sekund med GAME
            }
        }
        Log.d(TAG, "Registered Sensor Listener")
    }

    private fun unregisterSensor() {
        Log.d(TAG, "Unregistered Sensor Listener")
        sensorManager.unregisterListener(this)
        mSensorThread.quitSafely()
    }

    /**
     * Initialize ViewModel
     */
    fun setMeasurementId(id: UUID) {
        measurementIdLiveData.value = id
    }

    fun setMeasurement(measurement: Measurement) {
        this.measurement = measurement
        timerDuration = measurement.duration_seconds.toLong() * 1000 //in ms
        _durationData.value = measurement.duration_seconds
        file = measurementRepository.getRawDataFile(measurement)
    }

    fun setNavController(navController: NavController) {
        this.navController = navController
    }

    /**
     * Button handling
     */
    fun onStop() {
        stopCollectingData()
        if (!isTimerFinished) {
            measurement.duration_seconds = actualDuration
            measurement.sampling_frequency = countLines / actualDuration
            measurementRepository.updateMeasurement(measurement)

            val action = CollectDataFragmentDirections
                    .actionNavigationCollectDataToStopMeasurementDialog()
            navController.navigate(action)
        }
    }

    /**
     * Dialog handling
     */

    fun onDiscard() {
        Log.d(TAG, "Discard pressed.")
        measurementLiveData.value?.let { measurementRepository.deleteMeasurement(it) }
    }

    fun onSave(): UUID {
        Log.d(TAG, "Save pressed.")
        return measurement.id
    }
}