package com.krisgun.vibra.util

import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.krisgun.vibra.R
import kotlin.math.log10

private const val TAG = "BindingAdapter"

@BindingAdapter("android:setLineChartData")
fun setLineChartData(view: LineChart, data: List<Pair<Long, Triple<Float, Float, Float>>>?) {
    view.setNoDataText("Loading graph data...")
    if (data != null) {
        if (data.isEmpty()) {
            view.setNoDataTextColor(ContextCompat.getColor(view.context, R.color.red))
            view.setNoDataText("No data found")
        } else {

            val entryListX: MutableList<Entry> = mutableListOf()
            val entryListY: MutableList<Entry> = mutableListOf()
            val entryListZ: MutableList<Entry> = mutableListOf()

            val initTime = data[0].first
            data.forEachIndexed { i, _ ->
                val dataPointTime = ((data[i].first - initTime).div(1000000000F))
                entryListX.add(Entry(dataPointTime, data[i].second.first))
                entryListY.add(Entry(dataPointTime, data[i].second.second))
                entryListZ.add(Entry(dataPointTime, data[i].second.third))
            }

            val xLineDataSet = LineDataSet(entryListX, "X (m/s^2)")
            xLineDataSet.axisDependency = YAxis.AxisDependency.LEFT
            xLineDataSet.setDrawCircles(false)

            val yLineDataSet = LineDataSet(entryListY, "Y (m/s^2)")
            yLineDataSet.axisDependency = YAxis.AxisDependency.LEFT
            yLineDataSet.color = R.color.purple_700
            yLineDataSet.setDrawCircles(false)

            val zLineDataSet = LineDataSet(entryListZ, "Z (m/s^2)")
            zLineDataSet.axisDependency = YAxis.AxisDependency.LEFT
            zLineDataSet.color = R.color.black
            zLineDataSet.setDrawCircles(false)

            val dataSets: MutableList<ILineDataSet> = mutableListOf()
            dataSets.add(xLineDataSet)
            dataSets.add(yLineDataSet)
            dataSets.add(zLineDataSet)

            val lineData = LineData(dataSets)

            val description = view.description
            description.text = "Accelerometer Data"

            view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.white))
            view.data = lineData
            view.invalidate()
        }
    }
}

@BindingAdapter("android:setTotAccLineChartData")
fun setTotAccLineChartData(view: LineChart, data: List<Pair<Long, Float>>?) {

    view.setNoDataText("Loading graph data...")
    if (data != null) {

        if (data.isEmpty()) {
            view.setNoDataTextColor(ContextCompat.getColor(view.context, R.color.red))
            view.setNoDataText("No data found")
        } else {

            val initTime = data[0].first
            val entryList: MutableList<Entry> = mutableListOf()

            data.forEach {
                val dataPointTime = ((it.first - initTime).div(1000000000F))
                entryList.add(Entry(dataPointTime, it.second))
            }

            val lineDataSet = LineDataSet(entryList, "sqrt(x^2 + y^2 + z^2) [ m/s^2 ]")
            lineDataSet.axisDependency = YAxis.AxisDependency.LEFT
            lineDataSet.setDrawCircles(false)

            val dataSets: MutableList<ILineDataSet> = mutableListOf()
            dataSets.add(lineDataSet)

            val lineData = LineData(dataSets)

            val description = view.description
            description.text = "Accelerometer Data"

            view.setBackgroundColor(view.resources.getColor(R.color.white))

            view.data = lineData
            view.invalidate()
        }
    }
}

@BindingAdapter(value = ["android:amplitudeSpectrumData", "android:amplitudeSpectrumPeaks"], requireAll = true)
fun setAmplitudeSpectrumLineChartData(view: LineChart, data: List<Pair<Double, Double>>?, peaks: List<Int>?) {
    view.setNoDataText("Loading graph data...")

    if (data != null) {

        if (data.isEmpty()) {
            view.setNoDataTextColor(ContextCompat.getColor(view.context, R.color.red))
            view.setNoDataText("No data found")
        } else {

            val entryList = mutableListOf<Entry>()
            val peakEntryList = mutableListOf<Entry>()

            data.forEach {
                entryList.add(Entry(it.first.toFloat(), it.second.toFloat()))
            }
            peaks?.forEach {
                peakEntryList.add(Entry(data[it].first.toFloat(), data[it].second.toFloat()))
            }

            val lineDataSet = LineDataSet(entryList, "Amplitude/Frequency  (|P1(f)| / Hz)")
            val peakDataSet = LineDataSet(peakEntryList, "Peaks")

            lineDataSet.axisDependency = YAxis.AxisDependency.LEFT
            lineDataSet.setDrawCircles(false)


            peakDataSet.axisDependency = YAxis.AxisDependency.LEFT
            peakDataSet.enableDashedLine(0F, 1F, 0F) //This will hide lines between points
            peakDataSet.setCircleColor(ContextCompat.getColor(view.context, R.color.red))
            peakDataSet.color = ContextCompat.getColor(view.context, R.color.red)
            peakDataSet.setDrawValues(false)

            val dataSets: MutableList<ILineDataSet> = mutableListOf()
            dataSets.add(lineDataSet)
            dataSets.add(peakDataSet)

            val lineData = LineData(dataSets)

            val description = view.description
            description.text = "Amplitude Spectrum"

            view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.white))

            view.data = lineData
            view.invalidate()
        }
    }
}

@BindingAdapter("android:powerSpectrumData")
fun setPowerSpectrumLineChartData(view: LineChart, data: List<Pair<Double, Double>>?) {
    view.setNoDataText("Loading graph data...")
    if (data != null) {
        if (data.isEmpty()) {
            view.setNoDataTextColor(ContextCompat.getColor(view.context, R.color.red))
            view.setNoDataText("No data found")
        } else {
            val entryList = mutableListOf<Entry>()

            data.forEach {
                entryList.add(Entry(it.first.toFloat(), 10 * log10(it.second.toFloat())))
            }

            val lineDataSet = LineDataSet(entryList, "Power/Frequency (dB/Hz)")

            lineDataSet.axisDependency = YAxis.AxisDependency.LEFT
            lineDataSet.setDrawCircles(false)

            val dataSets: MutableList<ILineDataSet> = mutableListOf()
            dataSets.add(lineDataSet)

            val lineData = LineData(dataSets)

            val description = view.description
            description.text = "Power Spectrum"

            view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.white))

            view.data = lineData
            view.invalidate()
        }
    }
}

@BindingAdapter("android:accelerationPeakOccurrences")
fun setAccelerationPeakOccurrences(view: BarChart, data: Map<Float, Int>?) {
    view.setNoDataText("Loading graph data...")

    if (data != null) {

        if (data.isEmpty()) {
            view.setNoDataTextColor(ContextCompat.getColor(view.context, R.color.red))
            view.setNoDataText("No data found")
        } else {
            val entryList = mutableListOf<BarEntry>()
            data.forEach { (acceleration, occurrences) ->
                entryList.add(BarEntry(acceleration, occurrences.toFloat()))
            }

            val barDataSet = BarDataSet(entryList, "Occurrences / Acceleration Peak (m/s^2)")
            barDataSet.barBorderWidth = 0.01f
            barDataSet.setDrawValues(false)
            val barData = BarData(barDataSet)
            barData.barWidth = 0.05f

            view.axisRight.isEnabled = false
            view.axisLeft.axisMinimum = 0F
            view.data = barData
            view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.white))
            view.description.text = "Peak Occurrences"
            view.setFitBars(true)
            view.invalidate()
        }
    }
}

