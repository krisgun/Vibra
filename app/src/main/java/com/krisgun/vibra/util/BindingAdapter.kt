package com.krisgun.vibra.util

import androidx.databinding.BindingAdapter
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import com.krisgun.vibra.data.Measurement

class BindingAdapter {

    @BindingAdapter("android:setLineChartData")
    fun setLineChartData(view: LineChart, data: List<Measurement>) {
        val entries = mutableListOf<LineData>()

        data.forEach { measurement ->

        }
    }
}