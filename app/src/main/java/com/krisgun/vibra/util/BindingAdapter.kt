package com.krisgun.vibra.util

import android.util.Log
import androidx.databinding.BindingAdapter
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import com.krisgun.vibra.data.Measurement

private const val TAG = "BindingAdapter"

@BindingAdapter("android:setLineChartData")
fun setLineChartData(view: LineChart, data: Array<FloatArray>?) {

    if (data != null) {
        Log.d(TAG, "data set: ${data.size} dim\n x: ${data[0].size}\n y: ${data[1].size}\n z: ${data[2].size}\n")
    }
    val entries = mutableListOf<LineData>()
}