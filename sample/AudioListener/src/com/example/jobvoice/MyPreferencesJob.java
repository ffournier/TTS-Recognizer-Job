package com.example.jobvoice;

import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.provider.Settings;

/**
 * Class Preference
 * implements Type Job Preference (ALL, SMS, CallBack, None)
 * @author florian
 *
 */
public class MyPreferencesJob extends PreferenceFragment {
	
	Preference accessibilityPreference;
	EditTextPreference retryPreference;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferencesjob);
		
		accessibilityPreference = (Preference) findPreference("accessibility_preference");
		accessibilityPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent();
				intent.setAction(Settings.ACTION_ACCESSIBILITY_SETTINGS);
				startActivity(intent);
				return false;
			}
		});
		
		retryPreference = (EditTextPreference) findPreference("retry_preference_job");
		retryPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				Integer value = (Integer) Integer.parseInt((String) newValue);
				if (value == null || value < 0) {
					newValue = 3;
				}
				return true;
			}
		});
	}
    
}
