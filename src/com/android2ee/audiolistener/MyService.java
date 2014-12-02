package com.android2ee.audiolistener;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

public class MyService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void askAnswer() {
		Thread t = new Thread(new Runnable() {
			
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
		t.start();
	}
	
	

}
