package com.android2ee.ttsjob.service;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.android2ee.ttsjob.TTSJobApplication;
import com.android2ee.ttsjob.activity.MyPreferences;
import com.android2ee.ttsjob.activity.MyPreferences.ValueList;
import com.android2ee.ttsjob.broadcast.AudioIntentReceiver;
import com.android2ee.ttsjob.broadcast.TTSJobBroadcast;
import com.android2ee.ttsjob.job.JobInterface;
import com.android2ee.ttsjob.job.JobManager;
import com.android2ee.ttsjob.job.Jobs;

/**
 * Service to manage the TTSJob
 * @author florian
 *
 */
public abstract class TTSJobService extends Service implements JobInterface {
	
	public static final String KEY_MESSAGE = "com.android2ee.ttsjob.message";
	public static final String KEY_NAME = "com.android2ee.ttsjob.name";
	
	// variable
	// save all POJOObjects to treat in queueMessage, contains type of object
	private ArrayList<POJOObject> myQueueMessage = new ArrayList<POJOObject>();
	
	// broadcast for
	TTSJobBroadcast broadcast;
	// Manager
	JobManager jobManager;
	
	// State of Service (if a Job Running)
	StateMessage state;
	
	OnAudioFocusChangeListener listenerAudioFocus;
	AudioFocusHelper audioHelper;
	
	AudioIntentReceiver receiverAudio;
	
	Handler mHandler;
	
	// silly, we must pass by a broadcast but i don't know how i do it
	int laterJobsCount;
	
	private enum StateMessage {
		IS_RUNNING,
		IS_NOT_RUNNING
	};
	
	private LocalBinder mBinder = new LocalBinder();

	/**
	 * Binder
	 * @author florian
	 *
	 */
	public class LocalBinder extends Binder {
		
		public TTSJobService getService() {
			return TTSJobService.this;
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	private Runnable myAttempts = new Runnable() {
		
		@Override
		public void run() {
			// we requets now the focus init
			if (audioHelper != null) {
				if ( audioHelper.requestFocus(listenerAudioFocus) ) {
					resumeSystem();
				}
			}
		}
	};
	
	private Runnable myAttemptsNew = new Runnable() {
		
		@Override
		public void run() {
			// we requets now the focus init
			newMessageInQueue();
		}
	};
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		state = StateMessage.IS_NOT_RUNNING;
		laterJobsCount = 0;
		mHandler = null;
		
		Log.i(getClass().getCanonicalName(), "TTSJobService onCreate");
		
		treatByType(MyPreferences.getType(this));
		// start the good manager
		/*if (isBluetooth()) {
			jobManager = new JobManagerBT(this, isPreferenceLanguage(), getTimeAfterStop());
		} else {*/
			jobManager = new JobManager(this/*, isPreferenceLanguage(), getTimeAfterStop()*/);
		//}
		
		listenerAudioFocus = new OnAudioFocusChangeListener() {
			
			@Override
			public void onAudioFocusChange(int focusChange) {
				if (mHandler != null) {
					mHandler.removeCallbacks(myAttempts);
				}
				Log.i(getClass().getCanonicalName(), "AUDIOFOCUS " + focusChange);
				if ( focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK  || 
						 focusChange == AudioManager.AUDIOFOCUS_LOSS || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
					//pause
					Log.i(getClass().getCanonicalName(), "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK || AUDIOFOCUS_LOSS || AUDIOFOCUS_LOSS_TRANSIENT");
					pauseSystem();
					// run the message after 5 seconds
					if (mHandler == null) {
						mHandler = new Handler();
					}
					mHandler.postDelayed(myAttempts, 5*1000);
				} else if ( focusChange == AudioManager.AUDIOFOCUS_GAIN || focusChange == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT ||
						focusChange == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK) {
					// normal
					Log.i(getClass().getCanonicalName(), "AUDIOFOCUS_GAIN || AUDIOFOCUS_GAIN_TRANSIENT || AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK");
					resumeSystem();
				} else {
					Log.i(getClass().getCanonicalName(), "AUDIOFOCUS unknow");
				}
				
			}
		};
	}
	
	/**
	 * Pause System TTSJob
	 */
	public void pauseSystem() {
		if (state == StateMessage.IS_RUNNING) {
			this.jobManager.pauseJob();
		} else {
			newMessageInQueue();
		}
	}
	
	/**
	 * Resume System TTSJob
	 */
	public void resumeSystem() {
		if (state == StateMessage.IS_RUNNING) {
			this.jobManager.resumeJob();
		}
	}
	
	// method abstract
	protected abstract boolean isBluetooth();
	protected abstract POJOObject getMetaData(Bundle bundle);
	//protected abstract Boolean isPreferenceLanguage();
	//protected abstract Long getTimeAfterStop();

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		Log.i(getClass().getCanonicalName(), "TTSJobService onStartCommand");
		int result =  super.onStartCommand(intent, flags, startId);
		if (intent != null) {
			if (intent.getExtras() != null) {
				// save POJOObject with type in Extras in queueMessage
				POJOObject object = getMetaData(intent.getExtras());
				if (object != null) {
					treatPOJOObject(MyPreferences.getType(this), object);
				}
			}
		} else {
			Log.w(getClass().getCanonicalName(), "TTSJobService No Intent");
		}
		return result;
	}
	
	/**
	 * Delete POJOObject Treated. (Fist)
	 */
	private void deleteProgressMessage() {
		if (myQueueMessage.size() > 0) {
			myQueueMessage.remove(0);
		}
	}

	// method abstract
	protected abstract Jobs addJobs(POJOObject object);
	
	/**
	 * start New Jobs in function of First POJOObject in queueMesage
	 */
	private void newMessageInQueue() {
		if (myQueueMessage.size() > 0) {
			boolean isOnPause = AudioFocusHelper.isOnPause(this);
			
			Log.i(getClass().getCanonicalName(), "newMessageInQueue isOnPause " + isOnPause);
			if (!isOnPause) {
				if(state == StateMessage.IS_NOT_RUNNING) {
					// start
					Jobs jobs;
					if (myQueueMessage.get(0).hasJob()) {
						jobs = myQueueMessage.get(0).getJobs();
					} else {
						jobs = addJobs(myQueueMessage.get(0));
					}
					if (jobs != null) {
						if (jobManager.startJobs(jobs, this)) {
							state = StateMessage.IS_RUNNING;
							initAudio();
						}
					}
				} else {
					resumeSystem();
				}
			} else {
				if(state == StateMessage.IS_NOT_RUNNING) {
					// test later
					if (mHandler == null) {
						mHandler = new Handler();
					} else {
						mHandler.removeCallbacks(myAttemptsNew);
					}
					mHandler.postDelayed(myAttemptsNew, 5* 1000);
				}
			}
		} else {
			
			// need to now if we have a runnable later which runs
			if (laterJobsCount <= 0) {
				// stop Service
				release();
				stopSelf();
			}
		}
	}
	
	
	/**
	 * init Audio receiver
	 */
	private void initAudio() {
		
		
		audioHelper = new AudioFocusHelper(this);
		
		audioHelper.requestFocus(listenerAudioFocus);
		receiverAudio = new AudioIntentReceiver(this);
		
		registerReceiver(receiverAudio, new IntentFilter(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY));
	}
	
	/**
	 * Stop Audio Receiver
	 */
	private void stopAudio() {
		
		if (audioHelper != null) {
			audioHelper.abandonFocus(listenerAudioFocus);
			audioHelper = null;
		}
		
		if (receiverAudio != null) {
			unregisterReceiver(receiverAudio);
			receiverAudio = null;
		}
	}
	
	/**
	 * Release manager
	 */
	private void release() {
		jobManager.release();
	}
	
	/**
	 * Save the POJOObject in queueMessage in function of type choosen by user
	 * @param value
	 * @param object
	 */
	protected void treatPOJOObject(ValueList value, POJOObject object) {
		TTSJobApplication app = TTSJobApplication.getInstance();
		if (value.getValue() == ValueList.NORMAL.getValue()) {
			Log.i(getClass().getCanonicalName(), "TTSJobService Normal new Message");
			myQueueMessage.add(object);
			newMessageInQueue();
		} else if (value.getValue() == ValueList.HEADSET.getValue()) {
			if (app.getHeadSet()) {
				Log.i(getClass().getCanonicalName(), "TTSJobService Headset new Message");
				myQueueMessage.add(object);
				newMessageInQueue();
			}
		} else if (value.getValue() == ValueList.HEADSET_BT.getValue()) {
			if (app.getHeadSetBT()) {
				Log.i(getClass().getCanonicalName(), "TTSJobService HeadsetBT new Message");
				myQueueMessage.add(object);
				newMessageInQueue();
			}
		} else if (value.getValue() == ValueList.NONE.getValue()) {
			// nothing
			Log.i(getClass().getCanonicalName(), "TTSJobService None Message");
		}
	}
	
	/**
	 * Treat type choosen by user
	 * @param value
	 */
	public void treatByType(ValueList value) {
		// if HeadSetBT, HeadSet, ALL, register broadcast
		if (value.getValue() <= ValueList.HEADSET.getValue()) {
			registerHeadSet();
		} else {
			unRegisterHeadSet();
		}
	}

	/**
	 * Register BroadCast
	 */
	private void registerHeadSet() {
		if (broadcast == null) {
			broadcast = new TTSJobBroadcast();
			registerReceiver(broadcast, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
		}
	}
	
	/**
	 * Unregister Broadcast
	 */
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
		stopAudio();
		
		if (mHandler != null) {
			mHandler.removeCallbacksAndMessages(null);
		}
		super.onDestroy();
	}

	@Override
	public void endJobs(int result) {
		// remove current POJOOBject
		Log.i(getClass().getCanonicalName(), "TTSJobService End Jobs");
		deleteProgressMessage();
		state = StateMessage.IS_NOT_RUNNING;
		stopAudio();
		// test now if we have a new Message in pending
		newMessageInQueue();
	}
	
	@Override
	public void reportJobs(Jobs jobs, long timer) {
		// remove current POJOOBject
		Log.i(getClass().getCanonicalName(), "TTSJobService End Jobs");
		deleteProgressMessage();
		state = StateMessage.IS_NOT_RUNNING;
		stopAudio();
		// start scheduler
		
		laterJobsCount++; 
		mHandler.postDelayed(new ReportRunnable(jobs), timer);
		// test now if we have a new Message in pending
		newMessageInQueue();
	}

	public class ReportRunnable implements Runnable {
		
		private Jobs jobs;

		public ReportRunnable(Jobs jobs) {
			super();
			this.jobs = jobs;
		}

		@Override
		public void run() {
			// we requets now the focus init
			// add job in queue
			myQueueMessage.add(new POJOJobs(jobs));
			// test now if we have a new Message in pending
			laterJobsCount--; 
			newMessageInQueue();
			
		}

	}
}
