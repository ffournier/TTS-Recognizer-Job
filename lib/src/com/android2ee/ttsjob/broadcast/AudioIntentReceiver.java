package com.android2ee.ttsjob.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android2ee.ttsjob.service.TTSJobService;

public class AudioIntentReceiver extends BroadcastReceiver {
	
	TTSJobService mService;
	
	public AudioIntentReceiver(TTSJobService service) {
		this.mService = service;
		
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		
		
		if (intent.getAction().equals(
                android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
			// signal your service to stop playback
			// (via an Intent, for instance)
			// TODO send a pause maybe ?
			Log.i(getClass().getCanonicalName(), "onReceive AudioIntent ACTION_AUDIO_BECOMING_NOISY" );
			//mService.pauseSystem();
		}

	}

}
