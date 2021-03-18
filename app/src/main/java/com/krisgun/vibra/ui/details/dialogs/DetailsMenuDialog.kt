package com.krisgun.vibra.ui.details.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.krisgun.vibra.databinding.DialogDetailsMenuBinding

class DetailsMenuDialog : BottomSheetDialogFragment() {

    private lateinit var binding: DialogDetailsMenuBinding
    private lateinit var navController: NavController
    private lateinit var detailsMenuViewModel: DetailsMenuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        detailsMenuViewModel =  ViewModelProvider(this).get(DetailsMenuViewModel::class.java)
        binding = DialogDetailsMenuBinding.inflate(
                inflater,
                container,
                false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            detailsMenuVM = detailsMenuViewModel
        }
        return binding.root
    }
}