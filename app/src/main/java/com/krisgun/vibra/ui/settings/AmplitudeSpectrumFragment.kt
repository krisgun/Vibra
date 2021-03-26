package com.krisgun.vibra.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.krisgun.vibra.R

class AmplitudeSpectrumFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_amplitude_spectrum, rootKey)
    }
}