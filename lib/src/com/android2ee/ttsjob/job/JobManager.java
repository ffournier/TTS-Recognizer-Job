package com.android2ee.ttsjob.job;

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
import android.util.Log;

public class JobManager implements RecognitionListener {
	
	Jobs jobs;
	Job job;
	
	Context context;
	
	TextToSpeech ttobj;
	SpeechRecognizer recognizer;
	Intent intentRecognizer;
	
	Handler mHandler = new Handler();
	
	JobInterface jInterface;
	
	Boolean isPreferenceLanguage;
	Long timeAfterStop;
	
	
	public JobManager(Context context) {
		super();
		this.context = context;
		this.jobs  = new Jobs();
		initTextToSpeech();
		initRecognizer();
		isPreferenceLanguage = null;
		timeAfterStop = null;
	}
	
	public JobManager(Context context, Boolean pref, Long time) {
		this(context);
		isPreferenceLanguage = pref;
		timeAfterStop = time;
	}
	
	private void initRecognizer() {
		intentRecognizer = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intentRecognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intentRecognizer.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
            "com.android2ee.ttsjob");
		if (isPreferenceLanguage != null) {
			intentRecognizer.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, isPreferenceLanguage);
		}
		if (timeAfterStop != null) {
			intentRecognizer.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, timeAfterStop);
			intentRecognizer.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, timeAfterStop);
		}
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
			Log.i(getClass().getCanonicalName(), "Speak Begin");
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
					Log.i(getClass().getCanonicalName(), "Speak onUtteranceCompleted");
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
		Log.i(getClass().getCanonicalName(), "Beginning of speech");
	}

	@Override
	public void onBufferReceived(byte[] buffer) {
	}

	@Override
	public void onEndOfSpeech() {
		Log.i(getClass().getCanonicalName(), "End of Speech");
	}

	@Override
	public void onError(int error) {
		Log.i(getClass().getCanonicalName(), "Error Speech: " + error);
		switch (error) {
		case SpeechRecognizer.ERROR_NO_MATCH:
		case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
			if (job != null && job.canRetry()) {
				job.addRetry();
				startJob(job);
			} else {
				if (jobs != null) {
					job = jobs.getNextJobInList();
					if (job != null) {
						startJob(job);
					} else {
						endJobs(Jobs.ERROR);
					}
				} else {
					endJobs(Jobs.ERROR);
				}
			}
			break;
		case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
			// Rerun Job
			if (job != null) {
				startJob(job);
			} else {
				endJobs(Jobs.ERROR);
			}
			break;
		case SpeechRecognizer.ERROR_AUDIO:
		case SpeechRecognizer.ERROR_CLIENT:
		default:
			// error recognizer we go out
			endJobs(Jobs.ERROR);
			break;
		}
	}
	
	@Override
	public void onEvent(int eventType, Bundle params) {
	}

	@Override
	public void onPartialResults(Bundle partialResults) {
	}

	@Override
	public void onReadyForSpeech(Bundle params) {
	}

	@Override
	public void onResults(Bundle results) {
		ArrayList<String> voiceResults = results
	                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
		 //
		Log.i(getClass().getCanonicalName(), "/************************************/");
		Log.i(getClass().getCanonicalName(), "results : ");
		for (String value :  voiceResults) {
			Log.i(getClass().getCanonicalName(), value);
		}
		Log.i(getClass().getCanonicalName(), "/************************************/");
		JobAnswer result = job.onResult(voiceResults);
		Log.i(getClass().getCanonicalName(), "result :" + result.name());
		job = jobs.getNextJob(job, result);
		if (job != null) {
			startJob(job);
		} else {
			endJobs(Jobs.NOK);
		}
	}
	
	@Override
	public void onRmsChanged(float rmsdB) {
		
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
