package com.krisgun.vibra.ui.collect_data

import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.krisgun.vibra.R
import com.krisgun.vibra.databinding.FragmentCollectDataBinding

class CollectDataFragment : Fragment() {

    private lateinit var binding: FragmentCollectDataBinding
    private lateinit var viewModel: CollectDataViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_collect_data,
                container,
                false
        )

        viewModel = ViewModelProvider(this).get(CollectDataViewModel::class.java)

        binding.apply {
            collectDataVM = viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.registerSensor()
    }

    override fun onPause() {
        super.onPause()
        viewModel.unregisterSensor()
    }
}