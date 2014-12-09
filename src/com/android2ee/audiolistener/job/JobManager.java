package com.android2ee.audiolistener.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;

public class JobManager implements RecognitionListener {
	
	Jobs jobs;
	Job job;
	
	Context context;
	
	TextToSpeech ttobj;
	SpeechRecognizer recognizer;
	Intent intentRecognizer;
	
	Handler mHandler = new Handler();
	
	JobInterface jInterface;
	
	public JobManager(Context context) {
		super();
		this.context = context;
		this.jobs  = new Jobs();
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
            .createSpeechRecognizer(context);
		recognizer.setRecognitionListener(this);
	}
	
	private void initTextToSpeech() {
		ttobj = new TextToSpeech(context, 
	      	      new TextToSpeech.OnInitListener() {
	      				@Override
	      				public void onInit(int status) {
	      					if(status != TextToSpeech.ERROR){
	      						// TODO maybe need to find te good locale ...
	             	            ttobj.setLanguage(Locale.FRENCH);
	             	            if (job != null) {
	             	            	startJob(job);
	             	            }
	             	        }   
	      				}
	      		  }
	      	);
	}
	
	protected void startReconizer() {
		startListenningRecognizer();
	}
	
	protected void startListenningRecognizer() {
		recognizer.startListening(intentRecognizer);
	}
	
	public boolean startJobs(Jobs jobs, JobInterface jInterface) {
		this.jInterface = jInterface;
		if (jobs != null) {
			this.jobs = jobs;
			Job job = jobs.getFirstJob();
			if (job != null) {
				return startJob(job);
			}
		}
		return false;
	}
	
	private boolean startJob(Job job) {
		this.job = job;
		if (this.job != null) {
			speakText();
			return true;
		}
		return false;
	}
	
	private void endJobs(int result) {
		if (jobs != null) {
			jobs.removeAll();
		}
		if (this.jInterface != null) {
			this.jInterface.endJobs(result);
		}
	}
	
	private void speakText(){
		if (job != null) {
			HashMap<String, String> myHashAlarm = new HashMap<String, String>();
			myHashAlarm = job.startTTS(myHashAlarm);
	        int res = ttobj.speak(job.getMessageTTS(), TextToSpeech.QUEUE_FLUSH, myHashAlarm);
			ttobj.setOnUtteranceCompletedListener(new OnUtteranceCompletedListener() {
				
				@Override
				public void onUtteranceCompleted(String utteranceId) {
					if (utteranceId.equalsIgnoreCase(job.getId())) {
						// start recognizer
							mHandler.post(new Runnable() {
							
							@Override
							public void run() {
								if (job.hasRecognizer()) {
									startReconizer();
								} else {
									job = jobs.getNextJob(job, JobAnswer.NO_VOICE_RECOGNIZE);
									if (job != null) {
										startJob(job);
									} else {
										endJobs(Jobs.OK);
									}
								}
							}
						});
					}
				}
			});
		}
	}
	
	@Override
	public void onBeginningOfSpeech() {
		// TODO Auto-generated method stub
		
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
	public void onError(int error) {
		if (error == 7) {
			endJobs(Jobs.ERROR);
		}
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
	public void onReadyForSpeech(Bundle params) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResults(Bundle results) {
		 ArrayList<String> voiceResults = results
	                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
		JobAnswer result = job.onResult(voiceResults);
		job = jobs.getNextJob(job, result);
		if (job != null) {
			startJob(job);
		} else {
			endJobs(Jobs.NOK);
		}
	}
	
	@Override
	public void onRmsChanged(float rmsdB) {
		// TODO Auto-generated method stub
		
	}
	
	public void release() {
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
}
