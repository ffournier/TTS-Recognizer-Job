package com.android2ee.audiolistener.bluetooth;

import android.content.Context;
import android.util.Log;

//inner class
//BluetoothHeadSetUtils is an abstract class that has
//4 abstracts methods that need to be implemented.
public class BluetoothHelper extends BluetoothHeadSetUtils
{
	 private BlueToothState listener;
	 
	 
	 public BluetoothHelper(Context context)
	 {
	     super(context);
	 }
	 
	 
	 @Override
	 public void onHeadsetDisconnected() {
		 Log.e("TAG", "onHeadsetDisconnected");
			
	 }


	 @Override
	 public void onHeadsetConnected() {
		 Log.e("TAG", "onHeadsetConnected");
		 
	 }


	 @Override
	 public void onScoAudioDisconnected() {
		 Log.e("TAG", "onScoAudioDisconnected");
	 }


	 @Override
	 public void onScoAudioConnected() {
		 Log.e("TAG", "onScoAudioConnected");
		 if (listener != null) {
			 Log.e("TAG", "onReady");
			 listener.onReady();
		 }
			 
	 }


	public void setOnBlueToothState( BlueToothState listener) {
		 Log.e("TAG", "BlueToothState");
    	this.listener = listener;
    }
}
