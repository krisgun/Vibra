package com.krisgun.vibra.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import com.krisgun.vibra.R
import com.krisgun.vibra.databinding.FragmentDetailsBinding

private const val NAVIGATE_FROM_COLLECT_DATA = "NAVIGATE_FROM_COLLECT_DATA"

class DetailsFragment : Fragment() {

    private lateinit var viewModel: DetailsViewModel
    private val args: DetailsFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val navController = findNavController()
                if (navController.previousBackStackEntry?.savedStateHandle?.get<Boolean>(NAVIGATE_FROM_COLLECT_DATA) == true) {
                    navController.popBackStack(R.id.navigation_measure, false)
                } else
                    navController.popBackStack()
            }
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(DetailsViewModel::class.java)

        val binding = FragmentDetailsBinding.inflate(
            inflater,
            container,
            false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            detailsVM = viewModel
        }
        passMeasurementIdToViewModel()
        return binding.root
    }


    private fun passMeasurementIdToViewModel() {
        val id = args.measurementId
        viewModel.setMeasurementId(id)
        viewModel.measurementLiveData.observe(viewLifecycleOwner,
            Observer { measurement ->
                measurement?.let {
                    viewModel.setMeasurement(measurement)
                }
            }
        )
    }
}