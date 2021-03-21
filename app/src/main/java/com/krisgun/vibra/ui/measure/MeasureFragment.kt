package com.krisgun.vibra.ui.measure

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.preference.PreferenceManager
import com.krisgun.vibra.R
import com.krisgun.vibra.databinding.FragmentMeasureBinding

private const val TAG = "MeasureView"

class MeasureFragment : Fragment() {

    private lateinit var measureViewModel: MeasureViewModel
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
        measureViewModel =
                ViewModelProvider(this).get(MeasureViewModel::class.java)

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
        measureViewModel.samplingFrequency = sharedPreferences.getInt(getString(R.string.prefs_sampling_frequency), 50)

        //Fetch duration minutes and seconds from shared preferences
        sharedMeasurePref = requireActivity().getSharedPreferences(getString(R.string.prefs_measure), Context.MODE_PRIVATE) ?: return
        val durationMinutes = sharedMeasurePref.getString(getString(R.string.prefs_measure_duration_minutes), "00")
        val durationSeconds = sharedMeasurePref.getString(getString(R.string.prefs_measure_duration_seconds), "15")
        val countdownSeconds = sharedMeasurePref.getString(getString(R.string.prefs_measure_countdown_seconds), "05")
        if (durationMinutes != null && durationSeconds != null && countdownSeconds != null) {
            measureViewModel.durationMinutes = durationMinutes
            measureViewModel.durationSeconds = durationSeconds
            measureViewModel.countdownSeconds = countdownSeconds
        }

        measureViewModel.setNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_app_bar_measure, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController)
                || return super.onOptionsItemSelected(item)
    }


}