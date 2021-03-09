package com.krisgun.vibra.ui.measure.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.krisgun.vibra.R

class MeasureSettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_measure, rootKey)
    }
}