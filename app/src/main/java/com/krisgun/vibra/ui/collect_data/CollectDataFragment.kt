package com.krisgun.vibra.ui.collect_data

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.krisgun.vibra.R
import com.krisgun.vibra.databinding.FragmentCollectDataBinding

private const val TAG = "CollectData"

class CollectDataFragment : Fragment() {

    private lateinit var binding: FragmentCollectDataBinding
    private val viewModel: CollectDataViewModel by navGraphViewModels(R.id.navigation_collect_and_dialog)

    private val args: CollectDataFragmentArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activity = activity as AppCompatActivity
        activity.supportActionBar?.hide()
        val bottomNavBar: BottomNavigationView? = activity?.findViewById(R.id.bottom_navigation)
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

        val activity = activity as AppCompatActivity
        activity.supportActionBar?.show()
        val bottomNavBar: BottomNavigationView? = activity?.findViewById(R.id.bottom_navigation)
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