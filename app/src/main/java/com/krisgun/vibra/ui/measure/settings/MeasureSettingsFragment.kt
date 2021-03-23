package com.krisgun.vibra.ui.measure.settings

import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import com.krisgun.vibra.R

class MeasureSettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_measure, rootKey)
        setOnBindEditTextListener()
    }

    private fun setOnBindEditTextListener() {
        preferenceManager
                .findPreference<EditTextPreference>(getString(R.string.prefs_sampling_frequency))
                ?.setOnBindEditTextListener { editText ->
                    editText.inputType = InputType.TYPE_CLASS_NUMBER
                    editText.filters = arrayOf(*editText.filters, InputFilter.LengthFilter(3))
                    editText.hint = getString(R.string.input_a_number)
                    editText.selectAll()
        }
    }
}