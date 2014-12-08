package com.android2ee.audiolistener.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract.PhoneLookup;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.telephony.SmsManager;
import android.util.Log;

import com.android2ee.audiolistener.MyApplication;
import com.android2ee.audiolistener.R;
import com.android2ee.audiolistener.activity.MainActivity.MyPreferences;
import com.android2ee.audiolistener.activity.MainActivity.ValueList;
import com.android2ee.audiolistener.bluetooth.BlueToothState;
import com.android2ee.audiolistener.bluetooth.BluetoothHelper;
import com.android2ee.audiolistener.broadcast.MyBroadcast;

public class MyService extends Service implements RecognitionListener {
	
	public static final String KEY_MESSAGE = "com.android2ee.audiolistener.message";
	public static final String KEY_NAME = "com.android2ee.audiolistener.name";
	
	private static final String UTTERANCE_MESSAGE_SMS_RECEIVED = "com.android2ee.audiolistener.message_received";
	private static final String UTTERANCE_MESSAGE_SMS_SEND = "com.android2ee.audiolistener.message_send";
	private static final String UTTERANCE_MESSAGE_SMS_TAKEN = "com.android2ee.audiolistener.message_taken";
	private static final String UTTERANCE_MESSAGE_NOTHING = "com.android2ee.audiolistener.message_nothing";
	
	TextToSpeech ttobj;
	SpeechRecognizer recognizer;
	Intent intentRecognizer;
	
	Handler mHandler = new Handler();
	
	StateMessage state;
	
	private ArrayList<POJOMessage> myQueueMessage = new ArrayList<POJOMessage>();
	
	MyBroadcast broadcast;
	BluetoothHelper helper;
	
	private enum StateMessage {
		DEFAULT,
		READ,
		SEND,
		NOTHING
	}
	
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
		state = StateMessage.NOTHING;
		
		
		treatSMSType(MyPreferences.getSMSType(this));
		
		initTextToSpeech();
		initRecognizer();
		
	}
	
	private void initRecognizer() {
		intentRecognizer = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intentRecognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intentRecognizer.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
            "com.android2ee.audiolistener");
		recognizer = SpeechRecognizer
            .createSpeechRecognizer(this);
		recognizer.setRecognitionListener(this);
	}
	
	private void initTextToSpeech() {
		ttobj = new TextToSpeech(this, 
	      	      new TextToSpeech.OnInitListener() {
	      				@Override
	      				public void onInit(int status) {
	      					if(status != TextToSpeech.ERROR){
	      						// TODO maybe need to find te good locale ...
	             	            ttobj.setLanguage(Locale.FRENCH);
	             	            if (isInProgress()) {
	             	            	speakText(getString(R.string.info_name, getNameinProgress()));
	             	            }
	      					}   
	      				}
	      		  }
	      	);
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
	
	private boolean isInProgress() {
		return state != StateMessage.NOTHING;
	}
	
	
	private void deleteProgressMessage() {
		if (myQueueMessage.size() > 0) {
			myQueueMessage.remove(0);
		}
	}
	
	private String getNameinProgress() {
		String result = "";
		if (myQueueMessage.size() > 0) {
			POJOMessage mes = myQueueMessage.get(0);
			result = mes.name;
			if (result == null || result.length() == 0) {
				result = mes.phoneNumber;
			}
		}
		return result;
	}
	
	private String getPhoneNumberinProgress() {
		if (myQueueMessage.size() > 0) {
			POJOMessage mes = myQueueMessage.get(0);
			return mes.phoneNumber;
		}
		return null;
	}
	
	private String getMessageinProgress() {
		if (myQueueMessage.size() > 0) {
			POJOMessage mes = myQueueMessage.get(0);
			return mes.message;
		} 
		return null;
	}
	
	private void startReconizer() {
		Log.e("TAG", "startListenning");
		if (MyPreferences.isMicBT(this)) {
			startBtMic();
		} else {
			recognizer.startListening(intentRecognizer);
		}
	}
	
	@Override
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
        }
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.e("TAG", "Ready for speech");
    }

    @Override
    public void onError(int error) {
        Log.e("TAG",
                "Error listening for speech: " + error);
        //recognizer.stopListening();
        // time out
        if (error == 7) {
        	state = StateMessage.NOTHING;
        	deleteProgressMessage();
        	stopBtMic();
        }
        
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.e("TAG", "Speech starting");
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onEndOfSpeech() {
        // TODO Auto-generated method stub
    	//recognizer.stopListening();
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRmsChanged(float rmsdB) {
        // TODO Auto-generated method stub

    }
	
	private void speakText(String message){
		Log.e("TAG", "speak Text" + message);
		HashMap<String, String> myHashAlarm = new HashMap<String, String>();
        if (state == StateMessage.DEFAULT) {
        	myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, UTTERANCE_MESSAGE_SMS_RECEIVED);
        } else if (state == StateMessage.READ) {
        	myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, UTTERANCE_MESSAGE_SMS_TAKEN);
        } else if (state == StateMessage.SEND) {
        	myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, UTTERANCE_MESSAGE_SMS_SEND);
        }else if (state == StateMessage.SEND) {
        	myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, UTTERANCE_MESSAGE_NOTHING);
        }
        
		int res = ttobj.speak(message, TextToSpeech.QUEUE_FLUSH, myHashAlarm);
		ttobj.setOnUtteranceCompletedListener(new OnUtteranceCompletedListener() {
			
			@Override
			public void onUtteranceCompleted(String utteranceId) {
				// TODO Auto-generated method stub
				Log.e("TAG", "passe par là " + utteranceId);
				if (utteranceId.equalsIgnoreCase(UTTERANCE_MESSAGE_SMS_RECEIVED)) {
					Log.e("TAG", "passe par là " + utteranceId);
					mHandler.post(new Runnable() {
						
						@Override
						public void run() {
							startReconizer();
						}
					});
				} else if (utteranceId.equalsIgnoreCase(UTTERANCE_MESSAGE_SMS_TAKEN)) {
					mHandler.post(new Runnable() {
						
						@Override
						public void run() {
							startReconizer();
						}
					});
				} else if (utteranceId.equalsIgnoreCase(UTTERANCE_MESSAGE_SMS_SEND)) {
					mHandler.post(new Runnable() {
						
						@Override
						public void run() {
							startReconizer();
						}
					});
				} else if (utteranceId.equalsIgnoreCase(UTTERANCE_MESSAGE_NOTHING)) {
					mHandler.post(new Runnable() {
						
						@Override
						public void run() {
							deleteProgressMessage();
							state = StateMessage.NOTHING;
							newMessageInQueue();
						}
					});
				}
			}
		});
		Log.e("TAG", "speak Text res" + res);
	}
	
	private void newMessageInQueue() {
		if (myQueueMessage.size() > 0) {
			if (state == StateMessage.NOTHING) {
				state = StateMessage.DEFAULT;
				speakText(getString(R.string.info_name, getNameinProgress()));
			}
		} else {
			release();
			//stopSelf();
		}
	}
	
	protected void sendSMSMessage(String message) {

	       try {
	         SmsManager smsManager = SmsManager.getDefault();
	         smsManager.sendTextMessage(getPhoneNumberinProgress(), null, message, null, null);
	         speakText(message + ". Message envoyé");
	      } catch (Exception e) {
	         speakText("Message non envoyé");
	      }
	}
	
	
	private void release() {
		if (recognizer != null) {
			recognizer.stopListening();
			recognizer.cancel();
			recognizer.destroy();
			recognizer = null;
		}
		if (ttobj != null) {
			ttobj.stop();
			ttobj.shutdown();
			ttobj = null;
		}
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
	
	private void startBtMic() {
		if (helper == null) {
			helper = new BluetoothHelper(this);
			
		}
		helper.setOnBlueToothState(new BlueToothState() {
			
			@Override
			public void onReady() {
				Log.e("TAG", "onReady");
				recognizer.startListening(intentRecognizer);
				
			}
			
		});
		helper.start();
	}
	
	/*public void testMic(boolean value) {
		if (value) {
			Log.e("TAG", "startBtMic");
			startBtMic();
		} else {
			Log.e("TAG", "stopBtMic");
			stopBtMic();
		}
	}*/
	
	private void stopBtMic() {
		if (helper != null) {
			helper.stop();
			helper.setOnBlueToothState(null);
			helper = null;
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
		stopBtMic();
		unRegisterHeadSet();
		super.onDestroy();
	}


	
	

}
