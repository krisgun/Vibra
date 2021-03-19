package com.krisgun.vibra.ui.details.dialogs

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
import com.krisgun.vibra.databinding.DialogDeleteMeasurementBinding

class DeleteMeasurementDialog : DialogFragment() {

    private lateinit var binding: DialogDeleteMeasurementBinding
    private val detailsMenuViewModel: DetailsMenuViewModel by navGraphViewModels(R.id.navigation_details_menu)
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogDeleteMeasurementBinding.inflate(
                inflater,
                container,
                false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            dialogFragment = this@DeleteMeasurementDialog
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    fun onDelete() {
        detailsMenuViewModel.deleteMeasurement()
        this.dismiss()
    }

    fun onCancel() {
        this.dismiss()
    }
}