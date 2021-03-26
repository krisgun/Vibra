package com.krisgun.vibra.ui.settings

import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import com.krisgun.vibra.R

class TotalAccelerationFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_total_acceleration, rootKey)
        setMaximaThresholdInputListener()
    }

    private fun setMaximaThresholdInputListener() {
        preferenceManager
                .findPreference<EditTextPreference>(getString(R.string.prefs_tot_acc_maxima_thresh_in_find_sign))
                ?.setOnBindEditTextListener { editText ->
                    editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                    editText.filters = arrayOf(*editText.filters, InputFilter.LengthFilter(5))
                    editText.hint = getString(R.string.input_a_number)
                    editText.selectAll()
                }
    }
}