package com.krisgun.vibra.ui.collect_data

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.krisgun.vibra.databinding.FragmentCollectDataBinding

class CollectDataFragment : Fragment() {

    private lateinit var collectDataViewModel: CollectDataViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        collectDataViewModel =
            ViewModelProvider(this).get(CollectDataViewModel::class.java)

        val binding = FragmentCollectDataBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            collectDataVM = collectDataViewModel
        }
        return binding.root
    }
}