package com.krisgun.vibra.ui.details.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.krisgun.vibra.R
import com.krisgun.vibra.databinding.DialogDetailsMenuBinding
import com.krisgun.vibra.ui.details.DetailsFragmentArgs

class DetailsMenuDialog : BottomSheetDialogFragment() {

    private lateinit var binding: DialogDetailsMenuBinding
    private lateinit var navController: NavController
    private val args: DetailsMenuDialogArgs by navArgs()
    private val viewModel: DetailsMenuViewModel by navGraphViewModels(R.id.navigation_details_menu)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogDetailsMenuBinding.inflate(
                inflater,
                container,
                false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            detailsMenuVM = viewModel
            dialogFragment = this@DetailsMenuDialog
        }
        passMeasurementIdToViewModel()
        return binding.root
    }

    fun onDelete() {
        val action = DetailsMenuDialogDirections
                .actionDetailsMenuDialogToDeleteMeasurementDialog()
        this.dismiss()
        navController.navigate(action)
    }

    fun onRename() {
        val action = DetailsMenuDialogDirections
            .actionDetailsMenuDialogToRenameMeasurementDialog()
        this.dismiss()
        navController.navigate(action)
    }

    private fun passMeasurementIdToViewModel() {
        val id = args.measurementId
        viewModel.setMeasurementId(id)
        viewModel.measurementLiveData.observe(viewLifecycleOwner,
                Observer { measurement ->
                    measurement?.let {
                        viewModel.measurement = measurement
                        viewModel.titleText = measurement.title
                    }
                }

        )
        viewModel.measurementsData.observe(viewLifecycleOwner, Observer {
            viewModel.measurementsList = it
        })
    }
}