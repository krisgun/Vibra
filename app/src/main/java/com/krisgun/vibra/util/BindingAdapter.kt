package com.krisgun.vibra.util

import android.graphics.Color
import android.util.Log
import androidx.databinding.BindingAdapter
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.krisgun.vibra.R
import com.krisgun.vibra.data.Measurement

private const val TAG = "BindingAdapter"

@BindingAdapter("android:setLineChartData")
fun setLineChartData(view: LineChart, data: Array<FloatArray>?) {
    view.setNoDataText("Loading Graph Data..")
    if (data != null) {
        val initTime = data[0][0]

        val xLineDataSet = LineDataSet(rawDataToEntryList(data[1], data[0], initTime), "xAcc (m/s^2)")
        xLineDataSet.axisDependency = YAxis.AxisDependency.LEFT
        xLineDataSet.setDrawCircles(false)

        val yLineDataSet = LineDataSet(rawDataToEntryList(data[2], data[0], initTime), "yAcc (m/s^2)")
        yLineDataSet.axisDependency = YAxis.AxisDependency.LEFT
        yLineDataSet.color = R.color.purple_700
        yLineDataSet.setDrawCircles(false)

        val zLineDataSet = LineDataSet(rawDataToEntryList(data[3], data[0], initTime), "zAcc (m/s^2)")
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

        view.setBackgroundColor(view.resources.getColor(R.color.white))

        view.data = lineData
        view.invalidate()
    }


}

fun rawDataToEntryList(dataArray: FloatArray, timeArray: FloatArray, initTime: Float): List<Entry> {

    val entryList: MutableList<Entry> = mutableListOf()

    dataArray.forEachIndexed { i, dataPointValue ->
        val dataPointTime = ((timeArray[i] - initTime) / 1000000000F)
        entryList.add(Entry(dataPointTime, dataPointValue))
    }
    return entryList.toList()
}