package com.android2ee.ttsjob.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * BroadCast on BootCompleted
 * @author florian
 *
 */
public abstract class TTSJobBootCompletedIntentReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
			   context.startService(callService(context));
		}
	}
	
	protected abstract Intent callService(Context context);

}
