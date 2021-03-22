package com.krisgun.vibra.ui.measure.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.krisgun.vibra.R
import com.krisgun.vibra.databinding.DialogCountdownBinding
import com.krisgun.vibra.ui.measure.MeasureViewModel

private const val TAG = "MeasureView"

class CountDownDialog: DialogFragment() {

    private lateinit var binding: DialogCountdownBinding
    private val viewModel: MeasureViewModel
        by navGraphViewModels(R.id.navigation_measure_countdown)
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
        Log.d(TAG, navController.currentDestination.toString())
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?):
            View? {
        binding = DialogCountdownBinding.inflate(
                inflater,
                container,
                false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            measureVM = viewModel
        }
        viewModel.startCountdown()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.countDownTimer.cancel()
    }
}