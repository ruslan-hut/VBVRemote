package ua.com.programmer.vbvremote.settings

import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import ua.com.programmer.vbvremote.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val serverAddress: EditTextPreference? = findPreference("server_address")
        serverAddress?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()

    }

}