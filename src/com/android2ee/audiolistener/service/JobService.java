package com.android2ee.audiolistener.service;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.android2ee.audiolistener.MyApplication;
import com.android2ee.audiolistener.activity.JobActivity.MyPreferences;
import com.android2ee.audiolistener.activity.JobActivity.ValueList;
import com.android2ee.audiolistener.broadcast.MyBroadcast;
import com.android2ee.audiolistener.job.JobInterface;
import com.android2ee.audiolistener.job.JobManagerBT;
import com.android2ee.audiolistener.job.Jobs;

public abstract class JobService extends Service implements JobInterface {
	
	public static final String KEY_MESSAGE = "com.android2ee.audiolistener.message";
	public static final String KEY_NAME = "com.android2ee.audiolistener.name";
	
	//StateMessage state;
	
	private ArrayList<POJOObject> myQueueMessage = new ArrayList<POJOObject>();
	
	MyBroadcast broadcast;
	JobManagerBT jobManagerBT;
	
	StateMessage state;
	
	private enum StateMessage {
		IS_RUNNING,
		IS_NOT_RUNNING
	};
	
	private LocalBinder mBinder = new LocalBinder();

	public class LocalBinder extends Binder {
		
		public JobService getService() {
			return JobService.this;
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
		
		treatByType(MyPreferences.getType(this));
		jobManagerBT = new JobManagerBT(this);
	}
	
	
	
	
	protected abstract POJOObject getMetaData(Bundle bundle);

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		int result =  super.onStartCommand(intent, flags, startId);
		if (intent != null) {
			if (intent.getExtras() != null) {
				POJOObject object = getMetaData(intent.getExtras());
				if (object != null) {
					treatPOJOObject(MyPreferences.getType(this), object);
				}
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

	protected abstract Jobs addJobs(POJOObject object);
	
	private void newMessageInQueue() {
		if (myQueueMessage.size() > 0) {
			if (state == StateMessage.IS_NOT_RUNNING) {
				// start
				
				Jobs jobs = addJobs(myQueueMessage.get(0));
				if (jobs != null) {
					if (jobManagerBT.startJobs(jobs, this)) {
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
		jobManagerBT.release();
	}
	
	protected void treatPOJOObject(ValueList value, POJOObject object) {
		MyApplication app = MyApplication.getInstance();
		if (value.getValue() == ValueList.NORMAL.getValue()) {
			myQueueMessage.add(object);
			newMessageInQueue();
		} else if (value.getValue() == ValueList.HEADSET.getValue()) {
			if (app.getHeadSet()) {
				myQueueMessage.add(object);
				newMessageInQueue();
			}
		} else if (value.getValue() == ValueList.HEADSET_BT.getValue()) {
			if (app.getHeadSetBT()) {
				myQueueMessage.add(object);
				newMessageInQueue();
			}
		} else if (value.getValue() == ValueList.NONE.getValue()) {
			// nothing
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
