package com.krisgun.vibra.ui.measure

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.krisgun.vibra.databinding.FragmentMeasureBinding

class MeasureFragment : Fragment() {

    private lateinit var measureViewModel: MeasureViewModel

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
        val navController = findNavController()
        measureViewModel.setNavController(navController)
    }
}