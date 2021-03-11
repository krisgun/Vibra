package com.krisgun.vibra.util

import android.graphics.Color
import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.slider.Slider
import com.krisgun.vibra.R
import com.krisgun.vibra.data.Measurement

private const val TAG = "BindingAdapter"

@InverseBindingAdapter(attribute = "android:value")
fun getSliderValue(slider: Slider) = slider.value

@BindingAdapter("android:valueAttrChanged")
fun setSliderListeners(slider: Slider, attrChange: InverseBindingListener) {
    slider.addOnChangeListener { _, _, _ ->
        attrChange.onChange()
    }
}

@BindingAdapter("android:setLineChartData")
fun setLineChartData(view: LineChart, data: List<Pair<Float, Triple<Float, Float, Float>>>?) {
    view.setNoDataText("Loading Graph Data..")
    if (data != null) {

        val entryListX: MutableList<Entry> = mutableListOf()
        val entryListY: MutableList<Entry> = mutableListOf()
        val entryListZ: MutableList<Entry> = mutableListOf()

        val initTime = data[0].first
        data.forEachIndexed { i, _ ->
            val dataPointTime = ((data[i].first - initTime) / 1000000000F)
            entryListX.add(Entry(dataPointTime, data[i].second.first))
            entryListY.add(Entry(dataPointTime, data[i].second.second))
            entryListZ.add(Entry(dataPointTime, data[i].second.third))
        }

        val xLineDataSet = LineDataSet(entryListX, "xAcc (m/s^2)")
        xLineDataSet.axisDependency = YAxis.AxisDependency.LEFT
        xLineDataSet.setDrawCircles(false)

        val yLineDataSet = LineDataSet(entryListY, "yAcc (m/s^2)")
        yLineDataSet.axisDependency = YAxis.AxisDependency.LEFT
        yLineDataSet.color = R.color.purple_700
        yLineDataSet.setDrawCircles(false)

        val zLineDataSet = LineDataSet(entryListZ, "zAcc (m/s^2)")
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

@BindingAdapter("android:setPCALineChartData")
fun setPCALineChartData(view: LineChart, data: List<Pair<Float, Float>>?) {

    view.setNoDataText("Loading Graph Data..")
    if (data != null) {

        val initTime = data[0].first
        val entryList: MutableList<Entry> = mutableListOf()

        data.forEach {
            val dataPointTime = ((it.first - initTime) / 1000000000F)
            entryList.add(Entry(dataPointTime, it.second))
        }

        val lineDataSet = LineDataSet(entryList, "Acc (m/s^2)")
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