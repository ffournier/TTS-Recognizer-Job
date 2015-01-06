package com.android2ee.ttsjob.bluetooth;

import android.content.Context;
import android.util.Log;

/**
 * BlueTooth helper
 * @author florian
 */
public class BluetoothHelper extends BluetoothHeadSetUtils
{
	 private BlueToothState listener;
	 
	 
	 public BluetoothHelper(Context context)
	 {
	     super(context);
	 }
	 
	 
	 @Override
	 public void onHeadsetDisconnected() {
		 Log.i("TAG", "onHeadsetDisconnected");
			
	 }


	 @Override
	 public void onHeadsetConnected() {
		 Log.i("TAG", "onHeadsetConnected");
		 
	 }


	 @Override
	 public void onScoAudioDisconnected() {
		 Log.i("TAG", "onScoAudioDisconnected");
	 }


	 @Override
	 public void onScoAudioConnected() {
		 Log.i("TAG", "onScoAudioConnected");
		 if (listener != null) {
			 Log.e("TAG", "onReady");
			 listener.onReady();
		 }
			 
	 }


	public void setOnBlueToothState( BlueToothState listener) {
	Log.i("TAG", "BlueToothState");
    	this.listener = listener;
    }
}
