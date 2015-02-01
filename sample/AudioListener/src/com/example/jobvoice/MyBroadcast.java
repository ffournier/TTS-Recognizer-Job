package com.example.jobvoice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

public class MyBroadcast extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equalsIgnoreCase(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
			Log.i(getClass().getCanonicalName(), "action : " + intent.getAction());
			// get SMS received
			receivedSMS(context, intent);
		} else {
			Log.w(getClass().getCanonicalName(), "action unknwon : " + intent.getAction());
			//sendMessage(context, "Test pour savoir si cela passe", montel);
		}
	}
	
	private void receivedSMS(Context context, Intent intent) {
		// Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();
 
        try {
             
            if (bundle != null) {
                 
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                 
                for (int i = 0; i < pdusObj.length; i++) {
                     
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                     
                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();
 
                    Log.i(getClass().getCanonicalName(), "senderNum: "+ senderNum + "; message: " + message);
                    sendMessage(context, message, senderNum);
        			break;
                } // end for loop
                
              } // bundle is null
 
        } catch (Exception e) {
            Log.e(getClass().getCanonicalName(), "Exception smsReceiver" +e);
             
        }
	}
	
	private void sendMessage(Context context, String message, String senderNum) {
		Intent service = new Intent(context, MyService.class);
		service.putExtra(MyService.KEY_NAME, MyService.KEY_SMS);
		service.putExtra(MyService.KEY_MESSAGE, message);
		service.putExtra(MyService.KEY_PHONE_NUMBER, senderNum);
		context.startService(service); 
		Log.i(getClass().getCanonicalName(), "Start Service");
	}
	
}
