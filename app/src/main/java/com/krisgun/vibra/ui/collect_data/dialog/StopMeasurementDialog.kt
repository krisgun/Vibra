package com.krisgun.vibra.ui.collect_data.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

import com.krisgun.vibra.databinding.DialogStopMeasurementBinding
import com.krisgun.vibra.ui.collect_data.CollectDataViewModel

private const val TAG = "CollectData"

class StopMeasurementDialog : DialogFragment() {

    private lateinit var binding: DialogStopMeasurementBinding
    private val collectDataViewModel: CollectDataViewModel by activityViewModels()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        binding = DialogStopMeasurementBinding.inflate(
                inflater,
                container,
                false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = collectDataViewModel
            dialogFragment = this@StopMeasurementDialog
        }
        return binding.root
    }

    fun onSaveButton() {
        val detailAction =
            StopMeasurementDialogDirections
                    .actionStopMeasurementDialogToNavigationDetails(collectDataViewModel.onSave())
        activity?.viewModelStore?.clear()
        navController.navigate(detailAction)
    }

    fun onDiscardButton() {
        collectDataViewModel.onDiscard()
        val measureAction = StopMeasurementDialogDirections
                .actionStopMeasurementDialogToNavigationMeasure()
        activity?.viewModelStore?.clear()
        navController.navigate(measureAction)
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onDiscardButton()
    }


}