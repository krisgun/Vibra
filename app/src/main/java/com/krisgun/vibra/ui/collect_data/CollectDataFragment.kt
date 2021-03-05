package com.krisgun.vibra.ui.collect_data

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.krisgun.vibra.R
import com.krisgun.vibra.databinding.FragmentCollectDataBinding
import java.util.*

private const val TAG = "CollectData"

class CollectDataFragment : Fragment() {

    private lateinit var binding: FragmentCollectDataBinding
    private val viewModel: CollectDataViewModel by activityViewModels()

    private val args: CollectDataFragmentArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bottomNavBar: BottomNavigationView? = activity?.findViewById(R.id.nav_view)
        if (bottomNavBar != null) {
            bottomNavBar.visibility = View.GONE
        }

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.onStop()
            }
        })
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

        binding.apply {
            collectDataVM = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        passMeasurementToViewModel()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.isTimerFinished) {
            val action = CollectDataFragmentDirections
                    .actionNavigationCollectDataToNavigationDetails(args.measurementId)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val bottomNavBar: BottomNavigationView? = activity?.findViewById(R.id.nav_view)
        if (bottomNavBar != null) {
            bottomNavBar.visibility = View.VISIBLE
        }
        activity?.viewModelStore?.clear()
    }

    private fun passMeasurementToViewModel() {
        viewModel.setNavController(findNavController())

        val id = args.measurementId
        viewModel.setMeasurementId(id)
        viewModel.measurementLiveData.observe(viewLifecycleOwner,
                { measurement ->
                    measurement?.let {
                        viewModel.setMeasurement(measurement)
                        viewModel.measurementLiveData.removeObservers(viewLifecycleOwner)
                        viewModel.startCollectingData()
                    }
                }
        )

    }
}