package com.android2ee.audiolistener.job;

import android.content.Context;
import android.util.Log;

import com.android2ee.audiolistener.activity.JobActivity.MyPreferences;
import com.android2ee.audiolistener.bluetooth.BlueToothState;
import com.android2ee.audiolistener.bluetooth.BluetoothHelper;

public class JobManagerBT extends JobManager {
	
	BluetoothHelper helper;
	
	public JobManagerBT(Context context) {
		super(context);
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
