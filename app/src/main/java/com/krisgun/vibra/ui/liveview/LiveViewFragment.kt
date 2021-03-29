package com.krisgun.vibra.ui.liveview

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
import com.krisgun.vibra.databinding.FragmentLiveviewBinding

private const val TAG = "LiveView"

class LiveViewFragment : Fragment() {

    private lateinit var viewModel: LiveViewViewModel
    private lateinit var binding: FragmentLiveviewBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        viewModel =
                ViewModelProvider(this).get(LiveViewViewModel::class.java)
        navController = findNavController()

        binding = FragmentLiveviewBinding
                .inflate(inflater, container, false).apply {
                    lifecycleOwner= viewLifecycleOwner
                    liveviewVM = viewModel
                }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getSharedPreferences()
        viewModel.registerSensor()
    }

    override fun onPause() {
        super.onPause()
        viewModel.unregisterSensor()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_app_bar_liveview, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController)
                || return super.onOptionsItemSelected(item)
    }

    private fun getSharedPreferences() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.getString(getString(R.string.prefs_sampling_frequency), "50")?.let {
            viewModel.samplingFrequency = it.toInt()
        }
    }
}