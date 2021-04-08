package com.krisgun.vibra.ui.details

import android.view.View
import androidx.lifecycle.*
import com.github.psambit9791.jdsp.signal.peaks.FindPeak
import com.github.psambit9791.jdsp.signal.peaks.Peak
import com.krisgun.vibra.data.Measurement
import com.krisgun.vibra.database.MeasurementRepository
import com.krisgun.vibra.util.DataNames
import com.krisgun.vibra.util.SignalProcessing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.RoundingMode
import java.util.UUID

private const val TAG = "DetailsView"

class DetailsViewModel : ViewModel() {

    private val measurementRepository = MeasurementRepository.get()
    private val measurementIdLiveData = MutableLiveData<UUID>()
    private lateinit var measurement: Measurement

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

    private val _accPeakOccurrencesLiveData = MutableLiveData<Map<Float, Int>>()
    val accPeakOccurrencesLiveData: LiveData<Map<Float, Int>>
        get() = _accPeakOccurrencesLiveData

    private val _amplitudeSpectrumLiveData = MutableLiveData<List<Pair<Double, Double>>>()
    val amplitudeSpectrumLiveData: LiveData<List<Pair<Double, Double>>>
        get() = _amplitudeSpectrumLiveData

    private val _amplitudeSpectrumPeaksLiveData = MutableLiveData<List<Int>>()
    val amplitudeSpectrumPeaksLiveData: LiveData<List<Int>>
        get() = _amplitudeSpectrumPeaksLiveData

    private val _powerSpectrumLiveData = MutableLiveData<List<Pair<Double, Double>>>()
    val powerSpectrumLiveData: LiveData<List<Pair<Double, Double>>>
        get() = _powerSpectrumLiveData

    private val _amplitudeSpectrumResonance = MutableLiveData<Pair<Double, Double>>()
    val amplitudeSpectrumResonance: LiveData<Pair<Double, Double>>
        get() = _amplitudeSpectrumResonance

    private val _powerSpectrumResonance = MutableLiveData<Pair<Double, Double>>()
    val powerSpectrumResonance: LiveData<Pair<Double, Double>>
        get() = _powerSpectrumResonance

    /**
     * Fetching UI-data
     */
    fun setMeasurementId(id: UUID) {
        measurementIdLiveData.value = id
    }

    fun setChartData(measurement: Measurement) {
            this.measurement = measurement
            viewModelScope.launch {
                withContext(Dispatchers.Default) {
                    val rawData = getRawAcceleration(measurement)
                            .also { _rawDataLiveData.postValue(it) }
                    val totAcc = getTotalAcceleration(rawData)
                            .also { _totAccelDataLiveData.postValue(it) }

                    async { setTotalAccelerationPeakOccurrences(totAcc) }

                    async {
                        getAmplitudeSpectrum(totAcc, measurement.sampling_frequency).also {
                            _amplitudeSpectrumResonance.postValue(it.maxByOrNull { axis -> axis.second })
                            _amplitudeSpectrumLiveData.postValue(it)
                            setAmplitudeSpectrumPeaks(it)
                        }
                    }

                    async {
                        getPowerSpectrumData(totAcc, measurement.sampling_frequency).also {
                            _powerSpectrumResonance.postValue(it.maxByOrNull { axis -> axis.second })
                            _powerSpectrumLiveData.postValue(it)
                        }
                    }
                }
            }
    }

    private suspend fun getRawAcceleration(measurement: Measurement): List<Pair<Long, Triple<Float, Float, Float>>> {
        return  measurementRepository.getRawAccDataFromFile(measurement)
    }

    private suspend fun getTotalAcceleration(rawData: List<Pair<Long, Triple<Float, Float, Float>>>):
            List<Pair<Long, Float>> {

        val totalAccelerationFile = measurementRepository.getTotalAccelerationFile(measurement)
        return if (totalAccelerationFile.exists()) {
            measurementRepository.getTotalAccelerationFromFile(measurement)
        } else {
            val totAccResult = SignalProcessing.totalAccelerationAmplitude(rawData, findSignIndexMaximaThreshold)
            val resultTuples: MutableList<Pair<Long, Float>> = mutableListOf()

            for (i in totAccResult.indices) {
                resultTuples.add(Pair(rawData[i].first, totAccResult[i]))
            }
            measurementRepository.writeTotalAccelerationToFile(measurement, resultTuples)
            resultTuples
        }
    }

    private fun getTotalAccelerationPeaks(totalAccelerationData: List<Pair<Long, Float>>): List<Int> {
        val accSignal = totalAccelerationData.map { it.second.toDouble() }.toDoubleArray()
        val fp = FindPeak(accSignal)
        val out: Peak = fp.detectPeaks()
        return out.filterByHeight(totAccPeakLowerThresh, totAccPeakUpperThresh).toList()
    }

    private fun setTotalAccelerationPeakOccurrences(totalAccelerationData: List<Pair<Long, Float>>) {
        val peaks = getTotalAccelerationPeaks(totalAccelerationData)
        if (peaks.isNullOrEmpty()) {
            _showAccPeakOccurrences.postValue(View.GONE)
            return
        }

        val peakValues = peaks.map {
            totalAccelerationData[it]
                    .second.toBigDecimal().setScale(1,  RoundingMode.HALF_UP)
        }

        val peakOccurrences = totalAccelerationData.filter {
            peakValues.contains(
                it.second.toBigDecimal().setScale(1, RoundingMode.HALF_UP)
            )
        }.groupingBy { it.second.toBigDecimal().setScale(1, RoundingMode.HALF_UP) }
                .eachCount()
                .mapKeys { it.key.toFloat() }
                .toSortedMap()

        _accPeakOccurrencesLiveData.postValue(peakOccurrences)
        _showAccPeakOccurrences.postValue(_graphVisibleLiveData.value?.get(DataNames.TOTAL_ACCELERATION.ordinal))
    }

    private suspend fun getAmplitudeSpectrum(totalAccelerationData: List<Pair<Long, Float>>, samplingFrequency: Double):
            List<Pair<Double, Double>> {

        return if (measurementRepository.getAmplitudeSpectrumFile(measurement).exists()) {
            measurementRepository.getAmplitudeSpectrumFromFile(measurement)

        } else {
            SignalProcessing.singleSidedAmplitudeSpectrum(totalAccelerationData, samplingFrequency)
                    .also { amplitudeSpectrum ->
                        measurementRepository.writeAmplitudeSpectrumToFile(measurement, amplitudeSpectrum)
            }
        }
    }

    private fun setAmplitudeSpectrumPeaks(amplitudeSpectrumData: List<Pair<Double, Double>>) {
        val p1Signal = amplitudeSpectrumData.map { it.second }.toDoubleArray()

        val fp = FindPeak(p1Signal)
        val out: Peak = fp.detectPeaks()
        val peaks = out.filterByHeight(ampSpecPeakLowerThresh, ampSpecPeakUpperThresh).toList()
        _amplitudeSpectrumPeaksLiveData.postValue(peaks)
    }

    private suspend fun getPowerSpectrumData(totalAccelerationData: List<Pair<Long, Float>>, samplingFrequency: Double):
            List<Pair<Double, Double>> {
        return if (measurementRepository.getPowerSpectrumFile(measurement).exists()) {
            measurementRepository.getPowerSpectrumFromFile(measurement)
        } else {
            SignalProcessing.powerSpectrum(totalAccelerationData, samplingFrequency).also {
                measurementRepository.writePowerSpectrumToFile(measurement, it)
            }
        }
    }


    /**
     * Preferences handling
     */
    // Total Acceleration
    var findSignIndexMaximaThreshold: Double = 0.8
    var totAccPeakUpperThresh: Double = 78.0
    var totAccPeakLowerThresh: Double = 10.0

    //Amplitude Spectrum
    var ampSpecPeakUpperThresh: Double = 10.0
    var ampSpecPeakLowerThresh: Double = 0.25

    //Graph Visibility
    private val _showAccPeakOccurrences = MutableLiveData<Int>()
    val showAccPeakOccurrences: LiveData<Int>
        get() = _showAccPeakOccurrences

    private val _graphVisibleLiveData = MutableLiveData<List<Int>>()
    val graphVisibleLiveData: LiveData<List<Int>>
        get() = _graphVisibleLiveData

    fun setGraphVisibleBooleans(isGraphVisiblePrefSet: MutableSet<String>) {
        val isGraphVisibleList = mutableListOf<Int>()
        val dataFiles = DataNames.values()
        dataFiles.forEach {
            if (isGraphVisiblePrefSet.contains(it.name)) {
                isGraphVisibleList.add(View.VISIBLE)
            } else {
                isGraphVisibleList.add(View.GONE)
            }
        }
        if (isGraphVisibleList[DataNames.TOTAL_ACCELERATION.ordinal] == View.GONE)
            _showAccPeakOccurrences.value = View.GONE
        _graphVisibleLiveData.value = isGraphVisibleList
    }
}