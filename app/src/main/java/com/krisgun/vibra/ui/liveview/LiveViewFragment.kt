package com.krisgun.vibra.ui.liveview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.krisgun.vibra.R

class LiveViewFragment : Fragment() {

    private lateinit var liveViewViewModel: LiveViewViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        liveViewViewModel =
                ViewModelProvider(this).get(LiveViewViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_liveview, container, false)
        val textView: TextView = root.findViewById(R.id.text_liveview)
        liveViewViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}