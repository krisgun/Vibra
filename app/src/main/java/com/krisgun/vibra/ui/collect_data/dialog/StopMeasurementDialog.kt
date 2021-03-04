package com.krisgun.vibra.ui.collect_data.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedCallback

import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.krisgun.vibra.R
import com.krisgun.vibra.data.Measurement
import com.krisgun.vibra.database.MeasurementRepository

import com.krisgun.vibra.databinding.DialogStopMeasurementBinding
import com.krisgun.vibra.databinding.FragmentDetailsBinding
import com.krisgun.vibra.ui.details.DetailsViewModel


class StopMeasurementDialog : DialogFragment() {

    private lateinit var binding: DialogStopMeasurementBinding
    private lateinit var navController: NavController
    private lateinit var viewModel: StopMeasurementViewModel
    private val args: StopMeasurementDialogArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //Nothing
            }
        })
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(StopMeasurementViewModel::class.java)

        val binding = DialogStopMeasurementBinding.inflate(
                inflater,
                container,
                false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            dialogVM = viewModel
        }
        passMeasurementIdToViewModel()
        return binding.root
    }

    private fun passMeasurementIdToViewModel() {
        val id = args.measurementId
        viewModel.setMeasurementIdAndNavController(id, navController)
        viewModel.measurementLiveData.observe(viewLifecycleOwner,
                Observer { measurement ->
                    measurement?.let {

                    }
                }
        )
    }


}