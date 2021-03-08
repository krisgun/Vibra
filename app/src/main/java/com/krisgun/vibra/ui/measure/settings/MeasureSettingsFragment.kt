package com.krisgun.vibra.ui.measure.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.krisgun.vibra.databinding.FragmentSettingsMeasureBinding

class MeasureSettingsFragment: Fragment() {

    lateinit var binding: FragmentSettingsMeasureBinding
    lateinit var viewModel: MeasureSettingsViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(MeasureSettingsViewModel::class.java)

        binding = FragmentSettingsMeasureBinding
            .inflate(inflater, container, false)
            .apply {
                measureSettingsVM = viewModel
                lifecycleOwner = viewLifecycleOwner
        }

        if (savedInstanceState == null) {
            viewModel.restorePreferences()
        }
        return binding.root
    }


}