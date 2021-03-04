package com.krisgun.vibra.ui.collect_data.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.DialogFragment

import com.krisgun.vibra.databinding.DialogStopMeasurementBinding


class StopMeasurementDialog : DialogFragment() {

    private lateinit var binding: DialogStopMeasurementBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return DialogStopMeasurementBinding.inflate(inflater, container, false).root
    }
}