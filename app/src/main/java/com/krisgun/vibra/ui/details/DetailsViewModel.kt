package com.krisgun.vibra.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.github.psambit9791.jdsp.transform.PCA
import com.krisgun.vibra.data.Measurement
import com.krisgun.vibra.database.MeasurementRepository
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

    private val _pcaDataLiveData = MutableLiveData<List<Pair<Float, Float>>>()
    val pcaDataLiveData: LiveData<List<Pair<Float, Float>>>
    get() = _pcaDataLiveData

    fun setMeasurementId(id: UUID) {
        measurementIdLiveData.value = id
    }

    fun setMeasurement(measurement: Measurement) {
        this.measurement = measurement
        val rawData = measurementRepository.getRawDataTuple(measurement)
        val pcaData = getPCAData(rawData)

        _rawDataLiveData.value = rawData
        _pcaDataLiveData.value = pcaData
    }

    private fun getPCAData(rawData: List<Pair<Float, Triple<Float, Float, Float>>>): List<Pair<Float, Float>> {
        val signalArray: Array<DoubleArray> = Array(rawData.size) {DoubleArray(3) {0.0} }

        for (i in rawData.indices) {
            signalArray[i][0] = rawData[i].second.first.toDouble()
            signalArray[i][1] = rawData[i].second.second.toDouble()
            signalArray[i][2] = rawData[i].second.third.toDouble()
        }

        val resultingComponents = 1
        val pca = PCA(signalArray, resultingComponents)
        pca.fit()
        val pcaResult = pca.transform()

        val pcaResultTuples: MutableList<Pair<Float, Float>> = mutableListOf()
        for (i in rawData.indices) {
            pcaResultTuples.add(Pair(rawData[i].first, pcaResult[i][0].toFloat()))
        }
        return pcaResultTuples.toList()
    }

}