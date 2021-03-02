package com.krisgun.vibra.ui.collect_data

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.krisgun.vibra.R
import com.krisgun.vibra.databinding.FragmentCollectDataBinding

class CollectDataFragment : Fragment() {

    private lateinit var binding: FragmentCollectDataBinding
    private lateinit var viewModel: CollectDataViewModel

    val args: CollectDataFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

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
        passMeasurementToViewModel()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onStop()
    }

    private fun passMeasurementToViewModel() {
        val id = args.measurementId
        viewModel.setMeasurementId(id)
        viewModel.measurementLiveData.observe(viewLifecycleOwner,
                { measurement ->
                    measurement?.let {
                        viewModel.setMeasurement(measurement)
                        viewModel.startCollectingData()
                    }
                }
        )
    }
}