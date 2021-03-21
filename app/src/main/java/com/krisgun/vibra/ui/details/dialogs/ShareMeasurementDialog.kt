package com.krisgun.vibra.ui.details.dialogs

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.krisgun.vibra.R
import com.krisgun.vibra.databinding.DialogShareMeasurementBinding

private const val TAG = "ShareMeasurement"

class ShareMeasurementDialog: BottomSheetDialogFragment() {

    private lateinit var binding: DialogShareMeasurementBinding
    private lateinit var navController: NavController
    private val viewModel: DetailsMenuViewModel by navGraphViewModels(R.id.navigation_details_menu)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DialogShareMeasurementBinding.inflate(
                inflater,
                container,
                false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            detailsMenuVM = viewModel
            dialogFragment = this@ShareMeasurementDialog
        }
        viewModel.handleShareButton()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    fun onShare() {
        val uriList = arrayListOf<Uri>()
        viewModel.getDataFiles().forEach { file  ->
            Log.d(TAG, file.absolutePath)
            uriList.add(
                    FileProvider.getUriForFile(
                            requireContext(),
                            "com.krisgun.vibra.fileprovider",
                            file
                    )
            )
        }
        Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList)
            type = "text/csv"
        }.also { intent ->
            val chooserIntent = Intent.createChooser(intent, "CSV Data")
            startActivity(chooserIntent)
        }
        this.dismiss()
    }

    fun onCancel() {
        this.dismiss()
    }
}