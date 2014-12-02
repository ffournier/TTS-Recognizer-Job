package com.android2ee.audiolistener;

import java.util.ArrayList;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Telephony;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {
	
	MyBroadcast broadcast;
	BluetoothHelper helper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		helper = new BluetoothHelper(this);
		helper.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(broadcast);
		
		
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		broadcast = new MyBroadcast(this,this);
		registerReceiver(broadcast, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
		registerReceiver(broadcast, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
		
	}
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		helper.stop();
	}

	protected void askAnswer(final String message) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Log.e("TAG", "startListening3");
				
				Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
		            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
				intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
		            "com.android2ee.audiolistener");
				Log.e("TAG", "startListening4");
				SpeechRecognizer recognizer = SpeechRecognizer
		            .createSpeechRecognizer(MyApplication.getContext());
				Log.e("TAG", "startListening5");
				RecognitionListener listener = new RecognitionListener() {
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
			                    if (match.equalsIgnoreCase("oui")) {
			                    	broadcast.speakText(message);
			                    	break;
			                    } else if (match.equalsIgnoreCase("non")) {
			                    	broadcast.speakText("Va te faire foutre connard");
			                    	break;
			                    }
			                }
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
				};
			    Log.e("TAG", "startListening1");
			    recognizer.setRecognitionListener(listener);
			    Log.e("TAG", "startListening2");
			    recognizer.startListening(intent);
			    Log.e("TAG", "startListening");
			}
		});
		
	}
}
