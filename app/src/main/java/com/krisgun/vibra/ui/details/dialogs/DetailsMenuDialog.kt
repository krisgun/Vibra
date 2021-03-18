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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.krisgun.vibra.databinding.DialogDetailsMenuBinding
import com.krisgun.vibra.ui.details.DetailsFragmentArgs

class DetailsMenuDialog : BottomSheetDialogFragment() {

    private lateinit var binding: DialogDetailsMenuBinding
    private lateinit var navController: NavController
    private val args: DetailsMenuDialogArgs by navArgs()
    private lateinit var viewModel: DetailsMenuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel =  ViewModelProvider(this).get(DetailsMenuViewModel::class.java)
        binding = DialogDetailsMenuBinding.inflate(
                inflater,
                container,
                false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            detailsMenuVM = viewModel
        }
        passMeasurementIdToViewModel()
        return binding.root
    }

    private fun passMeasurementIdToViewModel() {
        val id = args.measurementId
        viewModel.setMeasurementId(id)
        viewModel.measurementLiveData.observe(viewLifecycleOwner,
                Observer { measurement ->
                    measurement?.let {
                    }
                }
        )
    }
}