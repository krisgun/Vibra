package com.krisgun.vibra.ui.measure

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.krisgun.vibra.R

class MeasureFragment : Fragment() {

    private lateinit var measureViewModel: MeasureViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        measureViewModel =
                ViewModelProvider(this).get(MeasureViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_measure, container, false)
        /*val textView: TextView = root.findViewById(R.id.text_measure)
        measureViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/
        return root
    }
}