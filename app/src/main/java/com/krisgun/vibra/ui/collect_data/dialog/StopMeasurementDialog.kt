package com.krisgun.vibra.ui.collect_data.dialog

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.krisgun.vibra.R

import com.krisgun.vibra.databinding.DialogStopMeasurementBinding
import com.krisgun.vibra.ui.collect_data.CollectDataViewModel

private const val TAG = "CollectData"

class StopMeasurementDialog : DialogFragment() {

    private lateinit var binding: DialogStopMeasurementBinding
    private val collectDataViewModel: CollectDataViewModel by navGraphViewModels(R.id.navigation_collect_and_dialog)
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
            dialogFragment = this@StopMeasurementDialog
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    fun onSaveButton() {
        val detailAction =
            StopMeasurementDialogDirections
                    .actionStopMeasurementDialogToNavigationDetails(collectDataViewModel.onSave())
        navController.navigate(detailAction)
    }

    fun onDiscardButton() {
        collectDataViewModel.onDiscard()
        val measureAction = StopMeasurementDialogDirections
                .actionStopMeasurementDialogToNavigationMeasure()
        navController.navigate(measureAction)
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onDiscardButton()
    }


}