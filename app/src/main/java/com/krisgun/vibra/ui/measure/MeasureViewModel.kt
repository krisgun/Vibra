package com.krisgun.vibra.ui.measure

import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.databinding.Bindable
import androidx.databinding.BindingAdapter
import androidx.navigation.NavController
import com.krisgun.vibra.BR
import com.krisgun.vibra.R
import com.krisgun.vibra.util.ObservableViewModel

private const val TAG = "MeasureViewModel"
const val INITIAL_MINUTES = "01"
const val INITIAL_SECONDS = "30"
const val INITIAL_COUNTDOWN_SECONDS = "05"

class MeasureViewModel : ObservableViewModel() {

    private lateinit var navController: NavController

    @get:Bindable
    var durationMinutes: String = INITIAL_MINUTES
        set(value) {
            field = value
            notifyPropertyChanged(BR.durationMinutes)
        }

    @get:Bindable
    var durationSeconds: String = INITIAL_SECONDS
        set(value) {
            field = value
            notifyPropertyChanged(BR.durationSeconds)
        }

    @get:Bindable
    var countdownSeconds: String = INITIAL_COUNTDOWN_SECONDS
        set(value) {
            field = value
            notifyPropertyChanged(BR.countdownSeconds)
        }



    fun setNavController(navController: NavController) {
        this.navController = navController
    }

    fun onStart() {
        val action = MeasureFragmentDirections.
            actionNavigationMeasureToNavigationCollectData()
        navController.navigate(action)
    }

    /**
     * TODO: IMPLEMENT AFTER TEXT CHANGED. SINGLE DIGIT SHOULD BE PREPENDED WITH A 0. EX 1 -> 01
     */

    fun afterTextChangedDurationMinutesText(text: Editable) {
        afterTextChangedSetTimeText(text)?.let {
            durationMinutes = it
        }
    }

    fun afterTextChangedDurationSecondsText(text: Editable) {
        afterTextChangedSetTimeText(text)?.let {
            durationSeconds = it
        }
    }

    fun afterTextChangedCountdownSecondsText(text: Editable) {
        afterTextChangedSetTimeText(text)?.let {
            countdownSeconds = it
        }
    }

    private fun afterTextChangedSetTimeText(text: Editable): String? {
        if (text.isNotEmpty()) {
            val textToChange = text.toString().toInt()
            if (textToChange in 6..9 && text.length < 2) {
                textToChange.toString()
                return "0$textToChange"
            }
        }
        return null
    }

    fun onFocusChangedTimerValidation(view: View, hasFocus: Boolean) {
        val editText = view as EditText
        if(!hasFocus) {
            val text = editText.text
            val singleDigitString = "0$text"
            when (text.length) {
                0 -> editText.setText(R.string.empty_timer_input)
                1 -> editText.setText(singleDigitString)
            }
        }
    }

}