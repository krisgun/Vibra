package com.krisgun.vibra.ui.history

import android.view.View
import com.krisgun.vibra.data.Measurement

interface RecyclerViewClickListener {
    fun onRecyclerViewItemClick(view: View, measurement: Measurement)
}