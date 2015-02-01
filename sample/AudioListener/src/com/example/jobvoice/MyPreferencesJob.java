package com.example.jobvoice;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.jobvoice.R;

/**
 * Class Preference
 * implements Type Job Preference (ALL, SMS, CallBack, None)
 * @author florian
 *
 */
public class MyPreferencesJob extends PreferenceFragment {
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferencesjob);
		
	}
    
}
