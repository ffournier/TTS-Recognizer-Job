package com.android2ee.audiolistener.activity;

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
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.android2ee.audiolistener.R;
import com.android2ee.audiolistener.service.JobService;
import com.android2ee.audiolistener.service.JobService.LocalBinder;
import com.android2ee.audiolistener.service.mysms.MyService;

public class JobActivity extends PreferenceActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/*utton button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent service = new Intent(JobActivity.this, MyService.class);
				service.putExtra(MyService.KEY_MESSAGE, "mon message de test");
				service.putExtra(MyService.KEY_NAME, "montel");
				JobActivity.this.startService(service); 
			}
		});
		
		Button stopS = (Button) findViewById(R.id.button_stop);
		stopS.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent service = new Intent(JobActivity.this, MyService.class);
				JobActivity.this.stopService(service); 
			}
		});*/
		
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	public static class MyPreferences extends PreferenceFragment implements OnPreferenceChangeListener {
		
		private ListPreference listPreference;
		private SwitchPreference switchPreference;
		protected JobService mService;
		
		private ServiceConnection mConnection = new ServiceConnection() {
			
			@Override
			public void onServiceDisconnected(ComponentName name) {
				mService = null;
			}
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				LocalBinder binder = (LocalBinder) service;
	            mService = (JobService)binder.getService();
			}
		};

	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        // Load the preferences from an XML resource
	        addPreferencesFromResource(R.xml.preferences);
	        
	        listPreference = (ListPreference) findPreference("sms_preference");
	        listPreference.setOnPreferenceChangeListener(this);
	        
	        switchPreference = (SwitchPreference) findPreference("mic_bt_preference");
	        switchPreference.setOnPreferenceChangeListener(this);
	     }
	    
	    

		@Override
		public void onStart() {
			super.onStart();
			
			Intent service = new Intent(getActivity(), MyService.class);
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
										.getString("sms_preference", ValueList.NONE.getValueString()));
		}
		
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
