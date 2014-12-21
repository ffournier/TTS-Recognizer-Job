package com.android2ee.ttsjob.service;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.android2ee.ttsjob.TTSJobApplication;
import com.android2ee.ttsjob.activity.MyPreferences;
import com.android2ee.ttsjob.activity.MyPreferences.ValueList;
import com.android2ee.ttsjob.broadcast.TTSJobBroadcast;
import com.android2ee.ttsjob.job.JobInterface;
import com.android2ee.ttsjob.job.JobManager;
import com.android2ee.ttsjob.job.JobManagerBT;
import com.android2ee.ttsjob.job.Jobs;

public abstract class TTSJobService extends Service implements JobInterface {
	
	public static final String KEY_MESSAGE = "com.android2ee.ttsjob.message";
	public static final String KEY_NAME = "com.android2ee.ttsjob.name";
	
	//StateMessage state;
	
	private ArrayList<POJOObject> myQueueMessage = new ArrayList<POJOObject>();
	
	TTSJobBroadcast broadcast;
	JobManager jobManager;
	
	StateMessage state;
	
	private enum StateMessage {
		IS_RUNNING,
		IS_NOT_RUNNING
	};
	
	private LocalBinder mBinder = new LocalBinder();

	public class LocalBinder extends Binder {
		
		public TTSJobService getService() {
			return TTSJobService.this;
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
		
		Log.i(getClass().getCanonicalName(), "TTSJobService onCreate");
		
		treatByType(MyPreferences.getType(this));
		if (isBluetooth()) {
			jobManager = new JobManagerBT(this, isPreferenceLanguage(), getTimeAfterStop());
		} else {
			jobManager = new JobManager(this, isPreferenceLanguage(), getTimeAfterStop());
		}
		
	}
	
	protected abstract boolean isBluetooth();
	protected abstract POJOObject getMetaData(Bundle bundle);
	protected abstract Boolean isPreferenceLanguage();
	protected abstract Long getTimeAfterStop();

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		Log.i(getClass().getCanonicalName(), "TTSJobService onStartCommand");
		int result =  super.onStartCommand(intent, flags, startId);
		if (intent != null) {
			if (intent.getExtras() != null) {
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
	
	private void deleteProgressMessage() {
		if (myQueueMessage.size() > 0) {
			myQueueMessage.remove(0);
		}
	}

	protected abstract Jobs addJobs(POJOObject object);
	
	private void newMessageInQueue() {
		if (myQueueMessage.size() > 0) {
			if (state == StateMessage.IS_NOT_RUNNING) {
				// start
				
				Jobs jobs = addJobs(myQueueMessage.get(0));
				if (jobs != null) {
					if (jobManager.startJobs(jobs, this)) {
						state = StateMessage.IS_RUNNING;
					}
				}
			}
		} else {
			release();
			stopSelf();
		}
	}
	
	private void release() {
		jobManager.release();
	}
	
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
	
	public void treatByType(ValueList value) {
		if (value.getValue() <= ValueList.HEADSET.getValue()) {
			registerHeadSet();
		} else {
			unRegisterHeadSet();
		}
	}

	
	private void registerHeadSet() {
		if (broadcast == null) {
			broadcast = new TTSJobBroadcast();
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
		Log.i(getClass().getCanonicalName(), "TTSJobService End Jobs");
		deleteProgressMessage();
		state = StateMessage.IS_NOT_RUNNING;
		// test now if we have a new Message in pending
		newMessageInQueue();
	}

}
