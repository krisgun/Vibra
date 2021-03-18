package com.krisgun.vibra.ui.details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.github.psambit9791.jdsp.signal.peaks.FindPeak
import com.github.psambit9791.jdsp.signal.peaks.Peak
import com.krisgun.vibra.data.Measurement
import com.krisgun.vibra.database.MeasurementRepository
import com.krisgun.vibra.util.SignalProcessing
import java.util.UUID
import java.util.concurrent.Executors

private const val TAG = "DetailsView"

class DetailsViewModel : ViewModel() {

    private val measurementRepository = MeasurementRepository.get()
    private val measurementIdLiveData = MutableLiveData<UUID>()
    private lateinit var measurement: Measurement
    private val executor = Executors.newSingleThreadExecutor()

    val measurementLiveData: LiveData<Measurement?> =
        Transformations.switchMap(measurementIdLiveData) { id ->
            measurementRepository.getMeasurement(id)
        }

    /**
     * UI LiveData
     */
    private val _rawDataLiveData = MutableLiveData<List<Pair<Long, Triple<Float, Float, Float>>>>()
    val rawDataLiveData: LiveData<List<Pair<Long, Triple<Float, Float, Float>>>>
        get() = _rawDataLiveData

    private val _totAccelDataLiveData = MutableLiveData<List<Pair<Long, Float>>>()
    val totAccelDataLiveData: LiveData<List<Pair<Long, Float>>>
    get() = _totAccelDataLiveData

    private val _amplitudeSpectrumLiveData = MutableLiveData<List<Pair<Double, Double>>>()
    val amplitudeSpectrumLiveData: LiveData<List<Pair<Double, Double>>>
        get() = _amplitudeSpectrumLiveData

    private val _amplitudeSpectrumPeaksLiveData = MutableLiveData<List<Int>>()
    val amplitudeSpectrumPeaksLiveData: LiveData<List<Int>>
        get() = _amplitudeSpectrumPeaksLiveData

    private val _powerSpectrumLiveData = MutableLiveData<List<Pair<Double, Double>>>()
    val powerSpectrumLiveData: LiveData<List<Pair<Double, Double>>>
        get() = _powerSpectrumLiveData

    /**
     * Fetching UI-data
     */
    fun setMeasurementId(id: UUID) {
        measurementIdLiveData.value = id
    }

    fun setChartData(measurement: Measurement) {
        executor.execute {
            this.measurement = measurement

            val rawData = measurementRepository.getRawDataFromFile(measurement)
            _rawDataLiveData.postValue(rawData)

            val totalAccelerationData = getTotalAcceleration(rawData)
            _totAccelDataLiveData.postValue(totalAccelerationData)

            val amplitudeSpectrumData = getAmplitudeSpectrum(totalAccelerationData, measurement.sampling_frequency)
            _amplitudeSpectrumLiveData.postValue(amplitudeSpectrumData)

            val amplitudeSpectrumPeaks = getAmplitudeSpectrumPeaks(amplitudeSpectrumData)
            _amplitudeSpectrumPeaksLiveData.postValue(amplitudeSpectrumPeaks)

            val powerSpectrumData = getPowerSpectrumData(totalAccelerationData, measurement.sampling_frequency)
            _powerSpectrumLiveData.postValue(powerSpectrumData)
        }
    }

    private fun getTotalAcceleration(rawData: List<Pair<Long, Triple<Float, Float, Float>>>): List<Pair<Long, Float>> {

        val totalAccelerationFile = measurementRepository.getTotalAccelerationFile(measurement)

        return if (totalAccelerationFile.exists()) {
            measurementRepository.getTotalAccelerationFromFile(measurement)

        } else {
            val totAccResult = SignalProcessing.totalAccelerationAmplitude(rawData)
            val resultTuples: MutableList<Pair<Long, Float>> = mutableListOf()

            for (i in totAccResult.indices) {
                resultTuples.add(Pair(rawData[i].first, totAccResult[i]))
            }

            measurementRepository.writeTotalAccelerationToFile(measurement, resultTuples)
            resultTuples.toList()
        }
    }

    private fun getAmplitudeSpectrum(totalAccelerationData: List<Pair<Long, Float>>, samplingFrequency: Double):
            List<Pair<Double, Double>> {

        return if (measurementRepository.getAmplitudeSpectrumFile(measurement).exists()) {
            measurementRepository.getAmplitudeSpectrumFromFile(measurement)
        } else {
            SignalProcessing.singleSidedAmplitudeSpectrum(totalAccelerationData, samplingFrequency).also {
                measurementRepository.writeAmplitudeSpectrumToFile(measurement, it)
            }
        }
    }

    private fun getAmplitudeSpectrumPeaks(amplitudeSpectrumData: List<Pair<Double, Double>>): List<Int> {
        val p1Signal = amplitudeSpectrumData.map { it.second }.toDoubleArray()

        val fp = FindPeak(p1Signal)
        val out: Peak = fp.detectPeaks()
        return out.filterByProminence(0.5, 10.0).toList()
    }

    private fun getPowerSpectrumData(totalAccelerationData: List<Pair<Long, Float>>, samplingFrequency: Double):
            List<Pair<Double, Double>> {

        return if (measurementRepository.getPowerSpectrumFile(measurement).exists()) {
            measurementRepository.getPowerSpectrumFromFile(measurement)
        } else {
            SignalProcessing.powerSpectrum(totalAccelerationData, samplingFrequency).also {
                measurementRepository.writePowerSpectrumToFile(measurement, it)
            }
        }
    }

}