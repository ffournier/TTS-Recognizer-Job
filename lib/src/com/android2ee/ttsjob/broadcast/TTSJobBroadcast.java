package com.android2ee.ttsjob.broadcast;

import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android2ee.ttsjob.TTSJobApplication;

/**
 * BroadCast on ACTION_HEADSET_PLUG, ACTION_CONNECTION_STATE_CHANGED, ACTION_VENDOR_SPECIFIC_HEADSET_EVENT
 * @author florian
 *
 */
public class TTSJobBroadcast extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equalsIgnoreCase(Intent.ACTION_HEADSET_PLUG)) {
			Log.i("TAG", "action : " + intent.getAction());
			/** 
			 * state - 0 for unplugged, 1 for plugged.
				name - Headset type, human readable string
				microphone - 1 if headset has a microphone, 0 otherwise
			*/
			Bundle extras = intent.getExtras();
			Log.i("TAG", "state : " + extras.getInt("state"));
			Log.i("TAG", "name : " + extras.getString("name"));
			Log.i("TAG", "microphone : " + extras.getInt("microphone"));
			
			TTSJobApplication app = TTSJobApplication.getInstance();
			// set state HeadSet in application
			switch (extras.getInt("state")) {
            case 0:
                app.setHeadSet(false);
                break;
            case 1:
            	app.setHeadSet(true);
                break;
            }
			
		} else if (intent.getAction().equalsIgnoreCase(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)) {
			Log.i("TAG", "action : " + intent.getAction());
			TTSJobApplication app = TTSJobApplication.getInstance();
			int state = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, BluetoothHeadset.STATE_DISCONNECTED);
			Log.i("TAG", "action : " + state);
			// set state HeadSetBT in application
            if (state == BluetoothHeadset.STATE_AUDIO_CONNECTED || state == BluetoothProfile.STATE_CONNECTING || state == BluetoothProfile.STATE_CONNECTED) {
            	app.setHeadSetBT(true);
            } else if (state == BluetoothHeadset.STATE_AUDIO_DISCONNECTED || state == BluetoothProfile.STATE_DISCONNECTING || state == BluetoothProfile.STATE_DISCONNECTED){
            	app.setHeadSet(false);
            }
		} else if (intent.getAction().equalsIgnoreCase(BluetoothHeadset.ACTION_VENDOR_SPECIFIC_HEADSET_EVENT)) {
			Log.i("TAG", "action : " + intent.getAction());
		} else {
			Log.w("TAG", "action unknwon : " + intent.getAction());
			//sendMessage(context, "Test pour savoir si cela passe", montel);
		}
	}
	
	
	
}
