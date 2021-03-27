package com.krisgun.vibra.ui.settings

import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import com.krisgun.vibra.R

class AmplitudeSpectrumFragment : PreferenceFragmentCompat() {



    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_amplitude_spectrum, rootKey)

        listOf(
            getString(R.string.prefs_amp_spec_peak_upper_thresh),
            getString(R.string.prefs_amp_spec_peak_lower_thresh)
        ).forEach { setDecimalNumberInputListener(it) }
    }

    private fun setDecimalNumberInputListener(prefKey: String) {
        preferenceManager
            .findPreference<EditTextPreference>(prefKey)
            ?.setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                editText.filters = arrayOf(*editText.filters, InputFilter.LengthFilter(5))
                editText.hint = getString(R.string.input_a_number)
                editText.selectAll()
            }
    }
}