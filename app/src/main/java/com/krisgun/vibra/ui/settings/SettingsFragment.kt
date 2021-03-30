package com.krisgun.vibra.ui.settings

import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.krisgun.vibra.R
import kotlin.math.ceil

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
        setSamplingFrequencyInputListener()
        setOnTotalAccelerationClickListener()
        setOnAmplitudeSpectrumClickListener()
        setonResetPreferencesClickListener()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View? = super.onCreateView(inflater, container, savedInstanceState)
        view?.let {
            //Adjust bottom padding for navbar
            val density = requireActivity().resources.displayMetrics.density
            val bottomNavInPx = ceil(56.0 * density).toInt()
            listView.setPadding(0, 0, 0, bottomNavInPx)
        }
        return view
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

    private fun setonResetPreferencesClickListener() {
        preferenceManager.findPreference<Preference>(getString(R.string.prefs_reset_preferences))
                ?.setOnPreferenceClickListener {
                    preferenceManager.sharedPreferences.edit().clear().commit()
                    findNavController().navigateUp()
                    Toast.makeText(context, "Settings have now been reset.", Toast.LENGTH_SHORT).show()
                    true
        }
    }
}