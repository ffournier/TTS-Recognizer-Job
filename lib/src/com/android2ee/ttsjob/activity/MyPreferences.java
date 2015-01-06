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

/**
 * Class Preference
 * implements Type Preference (ALL, HeadSetBT, HadSet, None) and Mic possibility (pb)
 * @author florian
 *
 */
public class MyPreferences extends PreferenceFragment implements OnPreferenceChangeListener {
	
	// variable
	private ListPreference listPreference;
	private SwitchPreference switchPreference;
	protected TTSJobService mService;
	
	/**
	 * Class connection
	 */
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
		
		// start Service 
		// TODO maybe bind service if started ?
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
	
	/**
	 * get preferences Mic
	 * @param context
	 * @return
	 */
	public static boolean isMicBT(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("mic_bt_preference", false);
	}
	
	/**
	 * Get preferences type (All, HeadSetBT, HeadSet, None)
	 * @param context
	 * @return
	 */
	public static ValueList getType(Context context) {
		return ValueList.fromString(PreferenceManager.getDefaultSharedPreferences(context)
									.getString("type_preference", ValueList.NONE.getValueString()));
	}
	
	/**
	 * Enum Class for Type (All, HeadSet, HeadSetBT, None)
	 * @author florian
	 *
	 */
	public enum ValueList {
		NORMAL(1),
		HEADSET(2),
		HEADSET_BT(3),
		NONE(4);
		
		private int value;

		/**
		 * Constructor
		 * @param value
		 */
        private ValueList(int value) {
                this.value = value;
        }
        
        /**
         * Getter Value
         * @return
         */
        public int getValue() { return value;}
        
        /**
         * Getter String Value
         * @return
         */
        public String getValueString() { return String.valueOf(value);}
        
        /**
         * Map to store all value of Enum
         */
        private static final Map<Integer, ValueList> intToTypeMap = new HashMap<Integer, ValueList>();
        static {
            for (ValueList type : ValueList.values()) {
                intToTypeMap.put(type.value, type);
            }
        }

        /**
         * Static getter Value
         * @param i, value enum
         * @return
         */
        public static ValueList fromInt(int i) {
        	ValueList type = intToTypeMap.get(Integer.valueOf(i));
            if (type == null) 
                return ValueList.NONE;
            return type;
        }
        
        /**
         * Static getter Value
         * @param value: string value enum
         * @return
         */
        public static ValueList fromString(String value) {
        	ValueList type = intToTypeMap.get(Integer.parseInt(value));
            if (type == null) 
                return ValueList.NONE;
            return type;
        }
	}
	
}
