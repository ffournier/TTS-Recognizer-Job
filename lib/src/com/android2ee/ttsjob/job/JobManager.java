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

/**
 * Class which manage the jobs given
 * @author florian
 *
 */
public class JobManager implements RecognitionListener {
	
	// variable
	// all jobs to execute
	Jobs jobs;
	// currentJob
	Job job;
	
	// context
	Context context;
	
	// TTS and Recognizer
	TextToSpeech ttobj;
	SpeechRecognizer recognizer;
	Intent intentRecognizer;
	volatile boolean recognizerStarted;
	
	// handler to execute after the end of TTS.
	Handler mHandler = new Handler();
	
	JobInterface jInterface;
	
	Boolean isPreferenceLanguage;
	Long timeAfterStop;
	
	volatile boolean hasTreat;
	
	volatile boolean isOnPause;
	
	/**
	 * Constructor
	 * @param context
	 */
	public JobManager(Context context) {
		super();
		this.context = context;
		this.jobs  = new Jobs();
		initTextToSpeech();
		initRecognizer();
		isPreferenceLanguage = null;
		timeAfterStop = null;
		hasTreat = false;
		recognizerStarted = false;
		isOnPause = false;
	}
	
	/**
	 * Construtor
	 * @param context
	 * @param pref
	 * @param time
	 */
	public JobManager(Context context, Boolean pref, Long time) {
		this(context);
		isPreferenceLanguage = pref;
		timeAfterStop = time;
		hasTreat = true;
		recognizerStarted = false;
		isOnPause = false;
	}
	
	
	
	public boolean isRecognizerStarted() {
		return recognizerStarted;
	}

	/**
	 * init recognizer
	 */
	private void initRecognizer() {
		intentRecognizer = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intentRecognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intentRecognizer.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
            "com.android2ee.ttsjob");
		//intentRecognizer.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
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
	
	/**
	 * initTTS
	 */
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
	
	/**
	 * Start Recognizer
	 */
	protected void startReconizer() {
		startListenningRecognizer();
	}
	
	/**
	 * start listener on recognizer
	 */
	protected void startListenningRecognizer() {
		if (!isOnPause) {
			Log.i(getClass().getCanonicalName(), "startListenningRecognizer");
			hasTreat = false;
			if (recognizer != null) {
				recognizer.startListening(intentRecognizer);
				recognizerStarted = true;
			}
		}
	}
	
	/**
	 * Start Jobs
	 * @param jobs
	 * @param jInterface
	 * @return
	 */
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
	
	
	/**
	 * Pause Current Job
	 */
	public void pauseJob() {
		// if recognizer was started, we know that's him has the focus audio ;).
		if (!recognizerStarted) {
			Log.i(getClass().getCanonicalName(), "Pause Job");
			isOnPause = true;
			if (job != null) {
				stopJob();
			} else {
				endJobs(Jobs.ERROR);
			}
		} else {
			Log.i(getClass().getCanonicalName(), "Recognizer has focus audio");
		}
	}
	
	/**
	 * Resume Current Job
	 */
	public void resumeJob() {
		if (isOnPause) {
			isOnPause = false;
			if (job != null) {
				startJob(job);
			} else {
				endJobs(Jobs.ERROR);
			}
		}
	}
	
	/**
	 * Start a Job given
	 * @param job
	 * @return
	 */
	private boolean startJob(Job job) {
		this.job = job;
		if (this.job != null) {
			if (!isOnPause) {
				Log.i(getClass().getCanonicalName(), "Speak Begin");
				speakText();
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Stop Current Job
	 * @param job
	 */
	private void stopJob() {
		if (ttobj != null && ttobj.isSpeaking()) {
			ttobj.stop();
		}
		
		if ( recognizer != null && recognizerStarted == true ) {
			recognizer.stopListening();
		}
	}
	
	/**
	 * Call when the all jobs are executed
	 * @param result
	 */
	private void endJobs(int result) {
		if (jobs != null) {
			jobs.removeAll();
		}
		if (this.jInterface != null) {
			this.jInterface.endJobs(result);
		}
	}
	
	/**
	 * Speak TTS of the current Job
	 */
	private void speakText(){
		if (job != null && !isOnPause) {
			// save in map the current Job to found him later
			HashMap<String, String> myHashAlarm = new HashMap<String, String>();
			myHashAlarm = job.startTTS(myHashAlarm);
	        int res = ttobj.speak(job.getMessageTTS(), TextToSpeech.QUEUE_FLUSH, myHashAlarm);
			ttobj.setOnUtteranceCompletedListener(new OnUtteranceCompletedListener() {
				
				@Override
				public void onUtteranceCompleted(String utteranceId) {
					Log.i(getClass().getCanonicalName(), "Speak onUtteranceCompleted");
					// test which job was executed
					if (utteranceId.equalsIgnoreCase(job.getId())) {
						// start recognizer
							mHandler.post(new Runnable() {
							
							@Override
							public void run() {
								if (!isOnPause) {
									// start recognizer 
									if (job.hasRecognizer()) {
										startReconizer();
									} else {
										// next job
										job = jobs.getNextJob(job, JobAnswer.NO_VOICE_RECOGNIZE);
										if (job != null) {
											startJob(job);
										} else {
											endJobs(Jobs.OK);
										}
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
		if (!isOnPause) {
			// treat Error
			Log.i(getClass().getCanonicalName(), "Error Speech: " + error + " hasTreat " + hasTreat);
			if (!hasTreat) {
				hasTreat = true;
				switch (error) {
				case SpeechRecognizer.ERROR_NO_MATCH:
				case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
					// Retry if we can or pass to next job
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
					// restart job 
					//delayed message 1s
					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							if (job != null) {
								startJob(job);
							} else {
								endJobs(Jobs.ERROR);
							}
						}
					}, 1000);
					break;
				case SpeechRecognizer.ERROR_AUDIO:
				case SpeechRecognizer.ERROR_CLIENT:
				default:
					// error recognizer we go out
					endJobs(Jobs.ERROR);
					break;
				}
			}
		}
	}
	
	@Override
	public void onEvent(int eventType, Bundle params) {
	}

	@Override
	public void onPartialResults(Bundle partialResults) {
		// test the text listen in the current job
		ArrayList<String> voiceResults = partialResults
	                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
		 //
		Log.i(getClass().getCanonicalName(), "/************************************/");
		Log.i(getClass().getCanonicalName(), "Partials results : ");
		for (String value :  voiceResults) {
			Log.i(getClass().getCanonicalName(), value);
		}
		Log.i(getClass().getCanonicalName(), "/************************************/");
	}

	@Override
	public void onReadyForSpeech(Bundle params) {
	}

	@Override
	public void onResults(Bundle results) {
	
		recognizerStarted = false;
		
		if (!hasTreat) {
			if (!isOnPause) {
				hasTreat = true;
				// test the text listen in the current job
				ArrayList<String> voiceResults = results
			                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
				 //
				Log.i(getClass().getCanonicalName(), "/************************************/");
				Log.i(getClass().getCanonicalName(), "results : ");
				for (String value :  voiceResults) {
					Log.i(getClass().getCanonicalName(), value);
				}
				Log.i(getClass().getCanonicalName(), "/************************************/");
				// get answer of the current job
				Integer answer = job.onResult(voiceResults);
				Log.i(getClass().getCanonicalName(), "answer : " + answer);
				// get next job ( son or in list) in function of answer
				job = jobs.getNextJob(job, answer);
				if (job != null) {
					startJob(job);
				} else {
					endJobs(Jobs.NOK);
				}
			}
		}
	}
	
	@Override
	public void onRmsChanged(float rmsdB) {
		
	}
	
	/**
	 * Clean the manager
	 */
	public void release() {
		// clean the manager
		if (recognizer != null) {
			recognizer.stopListening();
			recognizer.cancel();
			recognizer.destroy();
			recognizerStarted = false;
			recognizer = null;
		}
		if (ttobj != null) {
			ttobj.stop();
			ttobj.shutdown();
			ttobj = null;
		}
	}
}
