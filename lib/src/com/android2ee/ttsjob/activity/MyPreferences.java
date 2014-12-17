package com.android2ee.ttsjob.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;

import com.android2ee.ttsjob.R;
import com.android2ee.ttsjob.service.TTSJobService;
import com.android2ee.ttsjob.service.TTSJobService.LocalBinder;

public class MyPreferences extends PreferenceFragment implements OnPreferenceChangeListener {
	
	private ListPreference listPreference;
	private SwitchPreference switchPreference;
	protected TTSJobService mService;
	
	private ServiceConnection mConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
            mService = (TTSJobService)binder.getService();
		}
	};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        
        listPreference = (ListPreference) findPreference("type_preference");
        listPreference.setOnPreferenceChangeListener(this);
        
        switchPreference = (SwitchPreference) findPreference("mic_bt_preference");
        switchPreference.setOnPreferenceChangeListener(this);
     }
    
    

	@Override
	public void onStart() {
		super.onStart();
		
		Intent service = new Intent(getActivity(), TTSJobService.class);
		getActivity().startService(service);
		getActivity().bindService(service, mConnection, Service.BIND_AUTO_CREATE);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		getActivity().unbindService(mConnection);
	}



	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference.getKey().equals(listPreference.getKey())) {
			// change headset
			if (mService != null) {
				mService.treatByType(ValueList.fromString((String)newValue));
				if (ValueList.fromString((String)newValue) != ValueList.HEADSET_BT) {
					switchPreference.setChecked(false);
					switchPreference.setEnabled(false);
				} else {
					switchPreference.setEnabled(true);
				}
			}
		}  
		return true;
	}
	
	public static boolean isMicBT(Context context) {
		
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("mic_bt_preference", false);
	}
	
	public static ValueList getType(Context context) {
		return ValueList.fromString(PreferenceManager.getDefaultSharedPreferences(context)
									.getString("type_preference", ValueList.NONE.getValueString()));
	}
	
	
	public enum ValueList {
		NORMAL(1),
		HEADSET(2),
		HEADSET_BT(3),
		NONE(4);
		
		private int value;

        private ValueList(int value) {
                this.value = value;
        }
        
        public int getValue() { return value;}
        
        public String getValueString() { return String.valueOf(value);}
        
        private static final Map<Integer, ValueList> intToTypeMap = new HashMap<Integer, ValueList>();
        static {
            for (ValueList type : ValueList.values()) {
                intToTypeMap.put(type.value, type);
            }
        }

        public static ValueList fromInt(int i) {
        	ValueList type = intToTypeMap.get(Integer.valueOf(i));
            if (type == null) 
                return ValueList.NONE;
            return type;
        }
        
        public static ValueList fromString(String value) {
        	ValueList type = intToTypeMap.get(Integer.parseInt(value));
            if (type == null) 
                return ValueList.NONE;
            return type;
        }
	}
	
}
