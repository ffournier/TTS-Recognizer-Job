package com.android2ee.audiolistener;

import java.util.HashMap;
import java.util.Locale;

import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Telephony;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.telephony.SmsMessage;
import android.util.Log;

public class MyBroadcast extends BroadcastReceiver {
	
	TextToSpeech ttobj;
	String message;
	Boolean isInit;
	Context context;
	
	private MainActivity activity;
	
	public MyBroadcast() {
		super();
		isInit = false;
	}

	public MyBroadcast(MainActivity activity, Context context) {
		super();
		isInit = false;
		this.context = context;
		this.activity = activity;
		initTextToSpeech();
	}

	
	private void initTextToSpeech() {
		ttobj = new TextToSpeech(context.getApplicationContext(), 
	      	      new TextToSpeech.OnInitListener() {
	      				@Override
	      				public void onInit(int status) {
	      					if(status != TextToSpeech.ERROR){
	             	            ttobj.setLanguage(Locale.FRENCH);
	             	            isInit = true;
	             	            if (message != null) {
	             	            	speakTextInfo("", message);
	             	            	message = null;
	             	            }
	      					}   
	      				}
	      		  }
	      	);
	}
	
	
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
		} else if (intent.getAction().equalsIgnoreCase(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)) {
			Log.i("TAG", "action : " + intent.getAction());
			
		} else if (intent.getAction().equalsIgnoreCase(BluetoothHeadset.ACTION_VENDOR_SPECIFIC_HEADSET_EVENT)) {
			Log.i("TAG", "action : " + intent.getAction());
		} else if (intent.getAction().equalsIgnoreCase(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
			Log.i("TAG", "action : " + intent.getAction());
			// get SMS received
			if (ttobj == null) {
				this.context = context;
				initTextToSpeech();
			}
			receivedSMS(context, intent);
		} else {
			Log.w("TAG", "action unknwon : " + intent.getAction());
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
 
                    Log.e("SmsReceiver", "senderNum: "+ senderNum + "; message: " + message);
                     
                    if (isInit) {
                    	//String name = getNum(senderNum);
                    	speakTextInfo(senderNum, message);
                    } else {
                    	this.message = message;
                    }
                } // end for loop
              } // bundle is null
 
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);
             
        }
	}
	
	public void speakTextInfo(String name, String message){
    	Log.e("TAG", "name " + name);
    	this.message = message;
    	HashMap<String, String> myHashAlarm = new HashMap<String, String>();
        myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_ALARM));
        myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "Message");
        
    	ttobj.speak(context.getString(R.string.info_name, name), TextToSpeech.QUEUE_FLUSH, myHashAlarm);
    	ttobj.setOnUtteranceCompletedListener(new OnUtteranceCompletedListener() {
			
			@Override
			public void onUtteranceCompleted(String utteranceId) {
				Log.e("TAG", "passe par là " + utteranceId);
				askAnswer();
			}
		});
    	
    }
    
    private void askAnswer() {
    	if (activity != null) {
    		activity.askAnswer(message);
    	}
    	
    }
    
    public void speakText(String message){
    	ttobj.speak(message, TextToSpeech.QUEUE_FLUSH, null);
    	this.message = null;
    }
}
