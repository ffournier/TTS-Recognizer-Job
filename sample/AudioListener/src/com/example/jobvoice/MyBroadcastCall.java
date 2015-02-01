package com.example.jobvoice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MyBroadcastCall extends BroadcastReceiver {
	
	private static String lastState = TelephonyManager.EXTRA_STATE_IDLE;
	private static final String KEY_PHONE_NUMBER = "com.example.jobvoice.phonenumber";
	
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
		
		final Context ctx = context;
		String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		Log.i(getClass().getCanonicalName(), "State Calling " + state);
		
		if(state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
			
			Log.i(getClass().getCanonicalName(), "Last State Calling " + lastState);
			if (lastState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
				// miss Call
				Log.i(getClass().getCanonicalName(), "Send Message Calling");
				
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
						String phoneNumber = pref.getString(KEY_PHONE_NUMBER, "");
			        	
						Log.i(getClass().getCanonicalName(), "phoneNumber Calling " + phoneNumber);
						final Cursor c = ctx.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, CallLog.Calls.NUMBER + " LIKE ?", new String[]{phoneNumber}, null);
						if (c.moveToFirst()) {
						    final int type = c.getColumnIndex(CallLog.Calls.TYPE);
						    final int dircode = c.getInt(type);
	
						    switch (dircode) {
						        case CallLog.Calls.MISSED_TYPE:
						        	Log.i(getClass().getCanonicalName(), "MISSED_TYPE Calling");
						        	int columNumber = c.getColumnIndex(CallLog.Calls.NUMBER);
						        	String sendNum =c.getString(columNumber);
						        	sendMessage(ctx, sendNum);
						            break;
						        default:
						        	Log.i(getClass().getCanonicalName(), dircode + " Calling");
						        	break;
						    }
						}
						c.close();
						sendMessage(ctx, phoneNumber);
					}
				}, 500);
				
			}
        } else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
        	String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        	SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        	SharedPreferences.Editor edit = pref.edit();
        	edit.putString(KEY_PHONE_NUMBER, phoneNumber);
        	edit.commit();
        	
        	Log.i(getClass().getCanonicalName(), "phoneNumber Ringing " + phoneNumber);
        }
		
		lastState = state;
	}
	
	private void sendMessage(Context context, String senderNum) {
		if (ToolPref.treatCallBack(context)) {
			Intent service = new Intent(context, MyService.class);
			service.putExtra(MyService.KEY_NAME, MyService.KEY_INCOMINGCALL);
			service.putExtra(MyService.KEY_PHONE_NUMBER, senderNum);
			context.startService(service); 
			Log.i(getClass().getCanonicalName(), "Start Service Calling");
		}
	}
	
}
