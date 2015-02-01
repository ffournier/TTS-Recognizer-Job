package com.example.jobvoice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MyBroadcastCall extends BroadcastReceiver {
	
	private static String lastState = TelephonyManager.EXTRA_STATE_IDLE;
    
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equalsIgnoreCase(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
			Log.i("TAG", "action : " + intent.getAction());
			// get Call Incoming
			receivedCall(context, intent);
		} else {
			Log.w("TAG", "action unknwon : " + intent.getAction());
		}
	}
	
	private void receivedCall(Context context, Intent intent) {
		String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		Log.i(getClass().getCanonicalName(), "State Calling " + state);
		//
        
	    //sendMessage(context, phoneNumber);
		if(state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
			if (lastState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
				// miss Call
				String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER); 
				sendMessage(context, phoneNumber);
			}
        }
		
		lastState = state;
	}
	
	private void sendMessage(Context context, String senderNum) {
		Intent service = new Intent(context, MyService.class);
		service.putExtra(MyService.KEY_NAME, MyService.KEY_INCOMINGCALL);
		service.putExtra(MyService.KEY_PHONE_NUMBER, senderNum);
		context.startService(service); 
		Log.e("SmsReceiver", "Start Service");
	}
	
}
