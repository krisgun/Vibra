package com.krisgun.vibra.ui.measure

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.onNavDestinationSelected
import androidx.preference.PreferenceManager
import com.krisgun.vibra.R
import com.krisgun.vibra.databinding.FragmentMeasureBinding

private const val TAG = "MeasureView"

class MeasureFragment : Fragment() {

    private val measureViewModel: MeasureViewModel by navGraphViewModels(R.id.navigation_measure_countdown)
    private lateinit var navController: NavController
    private lateinit var sharedMeasurePref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navController = findNavController()
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentMeasureBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            measureVM = measureViewModel
        }

        setupViewModel()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        with (sharedMeasurePref.edit()) {
            putString(getString(R.string.prefs_measure_duration_minutes), measureViewModel.durationMinutes)
            putString(getString(R.string.prefs_measure_duration_seconds), measureViewModel.durationSeconds)
            putString(getString(R.string.prefs_measure_countdown_seconds), measureViewModel.countdownSeconds)
            apply()
        }
    }

    private fun setupViewModel() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.getString(getString(R.string.prefs_sampling_frequency), "50")?.let {
                    measureViewModel.samplingFrequency = it.toInt()
                }

        //Fetch duration minutes and seconds from shared preferences
        sharedMeasurePref = requireActivity().getSharedPreferences(getString(R.string.prefs_measure), Context.MODE_PRIVATE)
        val durationMinutes = sharedMeasurePref.getString(getString(R.string.prefs_measure_duration_minutes), "00")
        val durationSeconds = sharedMeasurePref.getString(getString(R.string.prefs_measure_duration_seconds), "00")
        val countdownSeconds = sharedMeasurePref.getString(getString(R.string.prefs_measure_countdown_seconds), "00")
        if (durationMinutes != null && durationSeconds != null && countdownSeconds != null) {
            measureViewModel.durationMinutes = durationMinutes
            measureViewModel.durationSeconds = durationSeconds
            measureViewModel.countdownSeconds = countdownSeconds
        }

        measureViewModel.titleMaxLength = resources.getInteger(R.integer.title_max_length)

        measureViewModel.setNavController(navController)

        measureViewModel.measurementsData.observe(viewLifecycleOwner, Observer {

        })
        measureViewModel.statusMessage.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_app_bar_measure, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController)
                || return super.onOptionsItemSelected(item)
    }


}