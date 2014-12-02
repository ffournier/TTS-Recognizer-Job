package com.android2ee.audiolistener;

import android.content.Context;

//inner class
//BluetoothHeadSetUtils is an abstract class that has
//4 abstracts methods that need to be implemented.
public class BluetoothHelper extends BluetoothHeadSetUtils
{
	 public BluetoothHelper(Context context)
	 {
	     super(context);
	 }
	
	 @Override
	 public void onScoAudioDisconnected()
	 {
	     // Cancel speech recognizer if desired
	 }
	
	 @Override
	 public void onScoAudioConnected()
	 {           
	     // Should start speech recognition here if not already started  
	 }
	
	 @Override
	 public void onHeadsetDisconnected()
	 {
	
	 }
	
	 @Override
	 public void onHeadsetConnected()
	 {
	
	 }
}
