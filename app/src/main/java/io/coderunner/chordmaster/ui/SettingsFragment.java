package io.coderunner.chordmaster.ui;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import io.coderunner.chordmaster.R;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}