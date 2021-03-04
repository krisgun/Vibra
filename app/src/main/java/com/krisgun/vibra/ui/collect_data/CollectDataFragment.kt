package com.krisgun.vibra.ui.collect_data

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.krisgun.vibra.R
import com.krisgun.vibra.databinding.FragmentCollectDataBinding

private const val TAG = "CollectData"

class CollectDataFragment : Fragment() {

    private lateinit var binding: FragmentCollectDataBinding
    private lateinit var viewModel: CollectDataViewModel

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

        viewModel = ViewModelProvider(this).get(CollectDataViewModel::class.java)

        binding.apply {
            collectDataVM = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        passMeasurementToViewModel()
        return binding.root
    }


    override fun onPause() {
        super.onPause()
        Log.d(TAG, "Pause Fragment")
        viewModel.stopCollectingAndCancelTimer()
    }

    override fun onStop() {
        super.onStop()

        val bottomNavBar: BottomNavigationView? = activity?.findViewById(R.id.nav_view)
        if (bottomNavBar != null) {
            bottomNavBar.visibility = View.VISIBLE
        }
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