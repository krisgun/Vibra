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
    private lateinit var accelerometerFileWriter: FileWriter
    private lateinit var accelerometerFile: File
    private lateinit var gyroscopeFileWriter: FileWriter
    private lateinit var gyroscopeFile: File

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
    private var timestamps = mutableListOf<Long>()

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

    init {
        _progressData.value = 0
    }

    /**
     * Data Collection handling
     */
    fun startCollectingData() {

        //Open file writer and register sensor
        Log.d(TAG, "Opening FileWriter with filename: ${accelerometerFile.name}")
        try {
            accelerometerFileWriter = FileWriter(accelerometerFile, true)
            if (accelerometerFile.length() == 0L) {
                accelerometerFileWriter.write("Timestamp (ns),X (m/s^2),Y (m/s^2),Z (m/s^2)\n")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        Log.d(TAG, "Opening FileWriter with filename: ${gyroscopeFile.name}")
        try {
            gyroscopeFileWriter = FileWriter(gyroscopeFile, true)
            if (gyroscopeFile.length() == 0L) {
                gyroscopeFileWriter.write("Timestamp (ns),X (rad/s),Y (rad/s),Z (rad/s)\n")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        registerSensor()
        startTimer(timerDuration)

    }

    fun stopCollectingData() {

        //Unregister listener and measure time
        unregisterSensor()
        if (timestamps.size > 1) {
            startTime = timestamps.first()
            endTime = timestamps.last()
            countLines = timestamps.size

            (endTime - startTime).also {
                if (actualDuration == 0) {
                    actualDuration = (it / 1000000000.0).roundToInt()

                    //Update measurement sampling frequency
                    measurement.sampling_frequency = (countLines / (it / 1000000000.0))
                    measurement.num_of_datapoints = countLines

                    Log.d(TAG, "Measured frequency: ${measurement.sampling_frequency}\tMeasured duration: ${(it / 1000000.0)} ms\t $countLines lines")
                }
            }
        } else {
            measurement.num_of_datapoints = countLines
        }


        // Close filewriter
        try {
            accelerometerFileWriter.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            gyroscopeFileWriter.close()
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

                //Update DB
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

            when(event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    timestamps.add(event.timestamp)
                    countLines++
                    accelerometerFileWriter.write(String.format(Locale.US,"%d,%f,%f,%f\n", event.timestamp, event.values[0], event.values[1], event.values[2]))
                }
                Sensor.TYPE_GYROSCOPE -> {
                    gyroscopeFileWriter.write(String.format(Locale.US,"%d,%f,%f,%f\n", event.timestamp, event.values[0], event.values[1], event.values[2]))
                }
            }

        }
    }

    private fun registerSensor() {
        mSensorThread = HandlerThread("Sensor Thread", Process.THREAD_PRIORITY_MORE_FAVORABLE)
        mSensorThread.start()
        mSensorHandler = Handler(mSensorThread.looper)

        sensorManager.let { sm ->

            val sensorDelay = ((1F / measurement.sampling_frequency) * 1000000F).roundToInt()
            Log.d(TAG, "Sensor delay: $sensorDelay")
            sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER).let {
                sm.registerListener(this, it, sensorDelay, mSensorHandler) //ca 50 per sekund med GAME
            }

            sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE).let {
               sm.registerListener(this, it, sensorDelay, mSensorHandler)
            }
        }
    }

    private fun unregisterSensor() {
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
        accelerometerFile = measurementRepository.getRawAccDataFile(measurement)
        gyroscopeFile = measurementRepository.getRawGyroDataFile(measurement)
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