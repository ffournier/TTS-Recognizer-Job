package com.android2ee.ttsjob.job;

import android.content.Context;
import android.util.Log;

import com.android2ee.ttsjob.activity.MyPreferences;
import com.android2ee.ttsjob.bluetooth.BlueToothState;
import com.android2ee.ttsjob.bluetooth.BluetoothHelper;

/**
 * Class to manage the BT
 * TODO pb with mic
 * @author florian
 *
 */
public class JobManagerBT extends JobManager {
	
	BluetoothHelper helper;
	
	/**
	 * Constructor
	 * @param context
	 */
	public JobManagerBT(Context context) {
		super(context);
	}
	
	/**
	 * Constructor
	 * @param context
	 * @param pref
	 * @param time
	 */
	public JobManagerBT(Context context, Boolean pref, Long time) {
		super(context, pref, time);
	}

	@Override
	protected void startReconizer() {
		Log.i("TAG", "startListenning");
		if (MyPreferences.isMicBT(context)) {
			// start mic
			startBtMic();
		} else {
			super.startReconizer();
		}
	}
	
	/**
	 * Start Mic BT
	 */
	private void startBtMic() {
		// start helper
		if (helper == null) {
			helper = new BluetoothHelper(context);
			
		}
		helper.setOnBlueToothState(new BlueToothState() {
			
			@Override
			public void onReady() {
				// when the bluetooth is ready start the recognizer listener
				startListenningRecognizer();
			}
		});
		helper.start();
	}
	
	/**
	 * Stop mic BT
	 */
	private void stopBtMic() {
		// stop mic
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
