package com.krisgun.vibra.ui.measure

import android.os.Bundle
import android.view.*
import android.widget.Toast
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.krisgun.vibra.R
import com.krisgun.vibra.databinding.FragmentMeasureBinding

class MeasureFragment : Fragment() {

    private lateinit var measureViewModel: MeasureViewModel
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
        measureViewModel =
                ViewModelProvider(this).get(MeasureViewModel::class.java)
        navController = findNavController()

        val binding = FragmentMeasureBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            measureVM = measureViewModel
        }

        setUpNavigation()
        return binding.root
    }

    private fun setUpNavigation() {
        measureViewModel.setNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_app_bar_measure, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                Toast.makeText(requireContext(), "Settings clicked!", Toast.LENGTH_SHORT).show()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}