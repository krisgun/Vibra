package com.krisgun.vibra.ui.details.dialogs

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
    private lateinit var sharedPrefsShare: SharedPreferences

    private val viewModel: DetailsMenuViewModel by navGraphViewModels(R.id.navigation_details_menu)
    private lateinit var sharedPrefsStrings: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()

        sharedPrefsStrings = listOf(
                getString(R.string.prefs_share_raw_data),
                getString(R.string.prefs_share_total_acceleration),
                getString(R.string.prefs_share_amplitude_spectrum),
                getString(R.string.prefs_share_power_spectrum)
        )
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
        getSharedPreferences()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        with (sharedPrefsShare.edit()) {
            sharedPrefsStrings.forEachIndexed { index, string ->
                putBoolean(string, viewModel.checkBoxBooleans[index].get())
            }
            apply()
        }
    }

    private fun getSharedPreferences() {
        val isCheckedPrefValues = mutableListOf<Boolean>()
        sharedPrefsShare = requireActivity().getSharedPreferences(getString(R.string.prefs_share), Context.MODE_PRIVATE)
        sharedPrefsStrings.forEach { string ->
            sharedPrefsShare
                    .getBoolean(string, false)
                    .also { isCheckedPrefValues.add(it) }
        }
        viewModel.setCheckBoxPreferences(isCheckedPrefValues)
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