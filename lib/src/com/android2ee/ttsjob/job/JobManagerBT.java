package com.android2ee.ttsjob.job;

import android.content.Context;
import android.util.Log;

import com.android2ee.ttsjob.activity.MyPreferences;
import com.android2ee.ttsjob.bluetooth.BlueToothState;
import com.android2ee.ttsjob.bluetooth.BluetoothHelper;

public class JobManagerBT extends JobManager {
	
	BluetoothHelper helper;
	
	public JobManagerBT(Context context) {
		super(context);
	}
	
	public JobManagerBT(Context context, Boolean pref, Long time) {
		super(context, pref, time);
	}

	@Override
	protected void startReconizer() {
		Log.e("TAG", "startListenning");
		if (MyPreferences.isMicBT(context)) {
			startBtMic();
		} else {
			super.startReconizer();
		}
	}
	
	private void startBtMic() {
		if (helper == null) {
			helper = new BluetoothHelper(context);
			
		}
		helper.setOnBlueToothState(new BlueToothState() {
			
			@Override
			public void onReady() {
				startListenningRecognizer();
			}
		});
		helper.start();
	}
	
	private void stopBtMic() {
		if (helper != null) {
			helper.stop();
			helper.setOnBlueToothState(null);
			helper = null;
		}
	}

	@Override
	public void release() {
		super.release();
		stopBtMic();
	}
}
