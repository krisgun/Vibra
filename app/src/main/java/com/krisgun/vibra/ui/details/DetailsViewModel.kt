package com.krisgun.vibra.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.github.psambit9791.jdsp.filter.Butterworth
import com.krisgun.vibra.data.Measurement
import com.krisgun.vibra.database.MeasurementRepository
import com.krisgun.vibra.util.SignalProcessing
import java.util.UUID

private const val TAG = "DetailsViewModel"

class DetailsViewModel : ViewModel() {

    private val measurementRepository = MeasurementRepository.get()
    private val measurementIdLiveData = MutableLiveData<UUID>()
    private lateinit var measurement: Measurement

    val measurementLiveData: LiveData<Measurement?> =
        Transformations.switchMap(measurementIdLiveData) { id ->
            measurementRepository.getMeasurement(id)
        }

    private val _rawDataLiveData = MutableLiveData<List<Pair<Float, Triple<Float, Float, Float>>>>()
    val rawDataLiveData: LiveData<List<Pair<Float, Triple<Float, Float, Float>>>>
        get() = _rawDataLiveData

    private val _totAccelDataLiveData = MutableLiveData<List<Pair<Float, Float>>>()
    val totAccelDataLiveData: LiveData<List<Pair<Float, Float>>>
    get() = _totAccelDataLiveData

    fun setMeasurementId(id: UUID) {
        measurementIdLiveData.value = id
    }

    fun setMeasurement(measurement: Measurement) {
        this.measurement = measurement
        val rawData = measurementRepository.getRawDataTuple(measurement)
        _rawDataLiveData.value = rawData

        val totalAccelerationData = getTotalAcceleration(rawData)
        _totAccelDataLiveData.value = totalAccelerationData
    }

    private fun getTotalAcceleration(rawData: List<Pair<Float, Triple<Float, Float, Float>>>): List<Pair<Float, Float>> {

        val totAccResult = SignalProcessing.totalAccelerationAmplitude(rawData)
        /**
         * TODO: Save result to file when done and check if already such file exists.
         */

        /**
         * LowPass Butterworth Filter
         */
        //var filterArray: DoubleArray = totAccResult.map { it.toDouble() }.toDoubleArray()
        //val lowpassFilter = Butterworth(filterArray, measurement.sampling_frequency.toDouble())
        //filterArray = lowpassFilter.lowPassFilter(12, 0.3)

        val resultTuples: MutableList<Pair<Float, Float>> = mutableListOf()

        for (i in totAccResult.indices) {
            resultTuples.add(Pair(rawData[i].first, totAccResult[i]))
        }
        return resultTuples.toList()
    }

}