package com.krisgun.vibra.ui.liveview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LiveViewViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the live view Fragment"
    }
    val text: LiveData<String> = _text
}