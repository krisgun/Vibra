package com.krisgun.vibra.ui.details.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.krisgun.vibra.R
import com.krisgun.vibra.databinding.DialogRenameMeasurementBinding

class RenameMeasurementDialog : DialogFragment() {

    private lateinit var binding: DialogRenameMeasurementBinding
    private lateinit var navController: NavController
    private val viewModel: DetailsMenuViewModel by navGraphViewModels(R.id.navigation_details_menu)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogRenameMeasurementBinding.inflate(
            inflater,
            container,
            false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            detailsMenuVM = viewModel
            dialogFragment = this@RenameMeasurementDialog
        }

        viewModel.statusMessage.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
            viewModel.titleLength = resources.getInteger(R.integer.title_max_length)
        })

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    fun onRename() {
        if (viewModel.renameMeasurement()) {
            this.dismiss()
        }
    }

    fun onCancel() {
        this.dismiss()
    }
}