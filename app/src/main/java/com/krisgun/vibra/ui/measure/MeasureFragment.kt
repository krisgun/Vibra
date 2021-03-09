package com.krisgun.vibra.ui.measure

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import androidx.preference.PreferenceManager
import com.krisgun.vibra.R
import com.krisgun.vibra.databinding.FragmentMeasureBinding

class MeasureFragment : Fragment() {

    private lateinit var measureViewModel: MeasureViewModel
    private lateinit var navController: NavController

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

        setUpNavigation()
        return binding.root
    }

    private fun setUpNavigation() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        measureViewModel.samplingFrequency = sharedPreferences.getInt(getString(R.string.prefs_sampling_frequency), 50)
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