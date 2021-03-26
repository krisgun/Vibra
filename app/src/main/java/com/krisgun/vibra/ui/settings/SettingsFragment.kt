package com.krisgun.vibra.ui.settings

import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import androidx.navigation.fragment.findNavController
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.krisgun.vibra.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
        setSamplingFrequencyInputListener()
        setOnTotalAccelerationClickListener()
        setOnAmplitudeSpectrumClickListener()
    }

    private fun setSamplingFrequencyInputListener() {
        preferenceManager
                .findPreference<EditTextPreference>(getString(R.string.prefs_sampling_frequency))
                ?.setOnBindEditTextListener { editText ->
                    editText.inputType = InputType.TYPE_CLASS_NUMBER
                    editText.filters = arrayOf(*editText.filters, InputFilter.LengthFilter(3))
                    editText.hint = getString(R.string.input_a_number)
                    editText.selectAll()
        }
    }

    private fun setOnTotalAccelerationClickListener() {
        preferenceManager
                .findPreference<Preference>(getString(R.string.prefs_total_acceleration))
                ?.setOnPreferenceClickListener {
                    val action = SettingsFragmentDirections
                            .actionSettingsFragmentToSettingsTotalAccelerationFragment()
                    val navController = findNavController()
                    navController.navigate(action)
                    true
                }
    }

    private fun setOnAmplitudeSpectrumClickListener() {
        preferenceManager
                .findPreference<Preference>(getString(R.string.prefs_amplitude_spectrum))
                ?.setOnPreferenceClickListener {
                    val action = SettingsFragmentDirections
                            .actionSettingsFragmentToAmplitudeSpectrumFragment()
                    val navController = findNavController()
                    navController.navigate(action)
                    true
                }
    }
}