package com.android2ee.audiolistener.service;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.ContactsContract.PhoneLookup;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.android2ee.audiolistener.MyApplication;
import com.android2ee.audiolistener.R;
import com.android2ee.audiolistener.activity.MainActivity.MyPreferences;
import com.android2ee.audiolistener.activity.MainActivity.ValueList;
import com.android2ee.audiolistener.broadcast.MyBroadcast;
import com.android2ee.audiolistener.job.JobInterface;
import com.android2ee.audiolistener.job.JobManagerBT;
import com.android2ee.audiolistener.job.Jobs;
import com.android2ee.audiolistener.job.list.JobReadSMS;
import com.android2ee.audiolistener.job.list.JobReceiveSMS;
import com.android2ee.audiolistener.job.list.JobSendSMS;
import com.android2ee.audiolistener.job.list.JobSentSMS;

public class MyService extends Service implements JobInterface {
	
	public static final String KEY_MESSAGE = "com.android2ee.audiolistener.message";
	public static final String KEY_NAME = "com.android2ee.audiolistener.name";
	
	//StateMessage state;
	
	private ArrayList<POJOMessage> myQueueMessage = new ArrayList<POJOMessage>();
	
	MyBroadcast broadcast;
	JobManagerBT jobManagerBT;
	
	StateMessage state;
	
	private enum StateMessage {
		IS_RUNNING,
		IS_NOT_RUNNING
	};
	
	private LocalBinder mBinder = new LocalBinder();

	public class LocalBinder extends Binder {
		
		public MyService getService() {
			return MyService.this;
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		state = StateMessage.IS_NOT_RUNNING;
		
		treatSMSType(MyPreferences.getSMSType(this));
		jobManagerBT = new JobManagerBT(this);
	}
	
	
	public String getContact(String phoneNumber) {
		ContentResolver cr = getContentResolver();
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		Cursor cursor = cr.query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);
	    if (cursor == null) {
	        return phoneNumber;
	    }
	    String contactName = null;
	    if(cursor.moveToFirst()) {
	        contactName = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));
	    }

	    if(cursor != null && !cursor.isClosed()) {
	        cursor.close();
	    }

	    return contactName != null ? contactName : phoneNumber;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		int result =  super.onStartCommand(intent, flags, startId);
		if (intent != null) {
			if (intent.getExtras() != null && intent.getExtras().containsKey(KEY_MESSAGE)) {
				String message = intent.getStringExtra(KEY_MESSAGE);
				String phoneNumber = intent.getStringExtra(KEY_NAME);
				String name = getContact(phoneNumber);
				
				treatReceivedSMS(MyPreferences.getSMSType(this), new POJOMessage(message, phoneNumber, name));
				
			}
		} else {
			Log.w("TAG", "No Intent");
		}
		return result;
	}
	
	private void deleteProgressMessage() {
		if (myQueueMessage.size() > 0) {
			myQueueMessage.remove(0);
		}
	}
	
	/*@Override
    public void onResults(Bundle results) {
        ArrayList<String> voiceResults = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (voiceResults == null) {
            Log.e("TAG", "No voice results");
        } else {
            Log.e("TAG", "Printing matches: ");
            for (String match : voiceResults) {
            	Log.e("TAG", match);
            	if (state == StateMessage.DEFAULT) {
	                Log.e("TAG", "DEFAULT " + match);
	                if (match.equalsIgnoreCase("oui") || match.equalsIgnoreCase("ouais")) {
	                	state = StateMessage.READ;
	                	stopBtMic();
	                	speakText(getMessageinProgress() + ". Voulez vous envoyer un message à l'envoyeur ?"); // display name  here ?
	                	break;
	                } else if (match.equalsIgnoreCase("non")) {
	                	state = StateMessage.NOTHING;
	                	stopBtMic();
	                	deleteProgressMessage();
	                	speakText("Va te faire foutre connard");
	                	break;
	                }
            	} else if (state == StateMessage.READ) {
            		Log.e("TAG", "READ " + match);
            		if (match.equalsIgnoreCase("oui") || match.equalsIgnoreCase("ouais")) {
            			state = StateMessage.SEND;
            			stopBtMic();
	                	speakText("Annoncez le message ?");
	                	break;
	                } else if (match.equalsIgnoreCase("non")) {
	                	state = StateMessage.NOTHING;
	                	stopBtMic();
	                	deleteProgressMessage();
	                	speakText("Va te faire foutre connard");
	                	break;
	                }
            	} else if (state == StateMessage.SEND) {
            		Log.e("TAG", "SEND " + match);
            		stopBtMic();
            		sendSMSMessage(match);
            	}
            }
            
            // no answer available need to test
            /*Log.e("TAG", "Passe Nothing available");
            state = StateMessage.NOTHING;
            release();
            stopSelf();*/
        /*}
    }*/

   
	private void newMessageInQueue() {
		if (myQueueMessage.size() > 0) {
			if (state == StateMessage.IS_NOT_RUNNING) {
				// start
				POJOMessage message = myQueueMessage.get(0);
				Jobs jobs = new Jobs();
				jobs.addJob(new JobReceiveSMS(getString(R.string.info_name, message.getValidateName())));
				jobs.addJob(new JobReadSMS(message.getMessage() + ". Voulez vous envoyer un message à l'envoyeur ?"));
				JobSentSMS jobSentSMS = new JobSentSMS();
				jobs.addJob(new JobSendSMS(message.getPhoneNumber(), jobSentSMS));
				if (jobManagerBT.startJobs(jobs, this)) {
					state = StateMessage.IS_RUNNING;
				}
			}
		} else {
			release();
			stopSelf();
		}
	}
	
	private void release() {
		jobManagerBT.release();
	}
	
	public void treatReceivedSMS(ValueList value, POJOMessage message) {
		MyApplication app = MyApplication.getInstance();
		if (value.getValue() == ValueList.NORMAL.getValue()) {
			myQueueMessage.add(message);
			newMessageInQueue();
		} else if (value.getValue() == ValueList.HEADSET.getValue()) {
			if (app.getHeadSet()) {
				myQueueMessage.add(message);
				newMessageInQueue();
			}
		} else if (value.getValue() == ValueList.HEADSET_BT.getValue()) {
			if (app.getHeadSetBT()) {
				myQueueMessage.add(message);
				newMessageInQueue();
			}
		} else if (value.getValue() == ValueList.NONE.getValue()) {
			// nothing
		}
	}
	
	public void treatSMSType(ValueList value) {
		if (value.getValue() <= ValueList.HEADSET.getValue()) {
			registerHeadSet();
		} else {
			unRegisterHeadSet();
		}
	}

	
	private void registerHeadSet() {
		if (broadcast == null) {
			broadcast = new MyBroadcast();
			registerReceiver(broadcast, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
		}
	}
	
	private void unRegisterHeadSet() {
		if (broadcast != null) {
			unregisterReceiver(broadcast);
			broadcast = null;
		}
	}
	
	@Override
	public void onDestroy() {
		release();
		unRegisterHeadSet();
		super.onDestroy();
	}

	@Override
	public void endJobs(int result) {
		// TODO with result
		deleteProgressMessage();
		state = StateMessage.IS_NOT_RUNNING;
	}

}
