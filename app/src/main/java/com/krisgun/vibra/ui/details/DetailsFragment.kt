package com.krisgun.vibra.ui.details

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected
import com.krisgun.vibra.R
import com.krisgun.vibra.databinding.FragmentDetailsBinding

private const val TAG = "DetailsView"

class DetailsFragment : Fragment() {

    private lateinit var viewModel: DetailsViewModel
    private lateinit var navController: NavController
    private val args: DetailsFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()

        setHasOptionsMenu(true)


        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (navController.previousBackStackEntry?.destination?.id  == R.id.navigation_collect_data) {
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
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        passMeasurementIdToViewModel()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_app_bar_detailed_view, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController)
                || return super.onOptionsItemSelected(item)
    }


    private fun passMeasurementIdToViewModel() {
        val id = args.measurementId
        viewModel.setMeasurementId(id)
        viewModel.measurementLiveData.observe(viewLifecycleOwner,
            Observer { measurement ->
                measurement?.let {
                    viewModel.setChartData(measurement)
                }
            }
        )
    }
}