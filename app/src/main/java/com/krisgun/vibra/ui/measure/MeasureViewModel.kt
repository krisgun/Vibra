package com.krisgun.vibra.ui.measure

import android.os.CountDownTimer
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.krisgun.vibra.BR
import com.krisgun.vibra.R
import com.krisgun.vibra.data.Measurement
import com.krisgun.vibra.database.MeasurementRepository
import com.krisgun.vibra.ui.measure.dialogs.CountDownDialogDirections
import com.krisgun.vibra.util.ObservableViewModel
import kotlin.math.ceil

private const val TAG = "MeasureView"

class MeasureViewModel : ObservableViewModel() {

    private lateinit var navController: NavController
    private lateinit var newMeasurement: Measurement
    lateinit var countDownTimer: CountDownTimer
    private val measurementRepository = MeasurementRepository.get()
    private var isSecondsZero = false
    private var isMinutesZero = false

    var isButtonDisabled = ObservableBoolean()
    var samplingFrequency: Int = 0

    @get:Bindable
    var durationMinutes: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.durationMinutes)
        }

    @get:Bindable
    var durationSeconds: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.durationSeconds)
        }

    @get:Bindable
    var countdownSeconds: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.countdownSeconds)
        }

    fun createMeasurement() {
        val measurementDuration: Int = (durationMinutes.toInt() * 60) + durationSeconds.toInt()
        //Create Measurement obj
        newMeasurement = Measurement(duration_seconds = measurementDuration, sampling_frequency = samplingFrequency.toDouble())
        //Add measurement to database
        measurementRepository.addMeasurement(newMeasurement)
    }

    fun setNavController(navController: NavController) {
        this.navController = navController
    }

    /**
     * Start Button OnClick-function
     */
    fun onStart() {
        if (countdownSeconds.toInt() > 0) {
            val action = MeasureFragmentDirections
                    .actionNavigationMeasureToCountDownDialog()
            navController.navigate(action)

        } else {
            createMeasurement()
            val action = MeasureFragmentDirections
                    .actionNavigationMeasureToNavigationCollect(newMeasurement.id)
            navController.navigate(action)
        }
    }


    /**
     * Input field handling
     */
    fun afterTextChangedDurationMinutesText(text: Editable) {
        val textString = text.toString()
        isMinutesZero = textString == "00" || textString == "0" || textString == ""
        isButtonDisabled.set(isMinutesZero && isSecondsZero)

        if (textString == "") isButtonDisabled.set(true)

        afterTextChangedSetTimeText(text)?.let {
            durationMinutes = it
        }
    }

    fun afterTextChangedDurationSecondsText(text: Editable) {
        val textString = text.toString()
        isSecondsZero = textString == "00" || textString == "0"
        isButtonDisabled.set(isMinutesZero && isSecondsZero)

        if (textString == "") isButtonDisabled.set(true)

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

    /**
     * Countdown handling
     */

    private val _countdownData = MutableLiveData<Int>()
    val countdownData: LiveData<Int>
        get() = _countdownData

    fun startCountdown(millisTimerDuration: Long = countdownSeconds.toLong()*1000) {
       countDownTimer = object : CountDownTimer(millisTimerDuration, 1000) {
            override fun onFinish() {

                _countdownData.postValue(0)

                createMeasurement()
                val action = CountDownDialogDirections
                        .actionCountDownDialogToNavigationCollectAndDialog(newMeasurement.id)
                navController.navigate(action)
            }
            override fun onTick(millisUntilFinished: Long) {
                _countdownData.postValue(ceil((millisUntilFinished / 1000.0)).toInt())
            }
        }.start()
    }

}