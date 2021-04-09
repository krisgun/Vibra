package com.krisgun.vibra.ui.details

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.krisgun.vibra.R
import com.krisgun.vibra.databinding.FragmentDetailsBinding

private const val TAG = "DetailsView"

class DetailsFragment : Fragment() {

    private lateinit var viewModel: DetailsViewModel
    private lateinit var navController: NavController
    private lateinit var binding: FragmentDetailsBinding
    private val args: DetailsFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()

        setHasOptionsMenu(true)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (navController.previousBackStackEntry?.destination?.id  == R.id.navigation_collect_data) {
                    navController.popBackStack(R.id.measureFragment, false)
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

        binding = FragmentDetailsBinding.inflate(
            inflater,
            container,
            false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            detailsVM = viewModel
        }
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setUpViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_app_bar_detailed_view, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.more -> navController.navigate(DetailsFragmentDirections
                    .actionNavigationDetailsToDetailsMenuDialog(viewModel.measurementLiveData.value!!.id))
            else -> item.onNavDestinationSelected(navController)
        }
        return super.onOptionsItemSelected(item)
    }


    private fun setUpViewModel() {
        getSharedPreferences()

        val id = args.measurementId
        viewModel.setMeasurementId(id)
        viewModel.measurementLiveData.observe(viewLifecycleOwner,
            Observer { measurement ->
                measurement?.let {
                    viewModel.setChartData(measurement)
                }
            }
        )
        viewModel.statusMessage.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let { message ->
                val view: View = requireActivity().window.decorView.findViewById(android.R.id.content)
                Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).apply {
                    setAction("Dismiss") { this.dismiss() }
                    show()
                }
            }
        })
    }

    private fun getSharedPreferences() {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPrefs.getStringSet(getString(R.string.prefs_show_graphs_key), mutableSetOf())
                ?.let { viewModel.setGraphVisibleBooleans(it) }

        //Total Acceleration
        sharedPrefs.getString(getString(R.string.prefs_tot_acc_maxima_thresh_in_find_sign), "0.8")
            ?.let { viewModel.findSignIndexMaximaThreshold = it.toDouble() }

        sharedPrefs.getString(getString(R.string.prefs_tot_acc_peak_upper_thresh), "78.0")
            ?.let { viewModel.totAccPeakUpperThresh = it.toDouble() }

        sharedPrefs.getString(getString(R.string.prefs_tot_acc_peak_lower_thresh), "10.0")
            ?.let { viewModel.totAccPeakLowerThresh = it.toDouble() }

        //Amplitude Spectrum
        sharedPrefs.getString(getString(R.string.prefs_amp_spec_peak_upper_thresh), "10.0")
            ?.let { viewModel.ampSpecPeakUpperThresh = it.toDouble() }
        sharedPrefs.getString(getString(R.string.prefs_amp_spec_peak_lower_thresh), "0.25")
            ?.let { viewModel.ampSpecPeakLowerThresh = it.toDouble() }
    }
}