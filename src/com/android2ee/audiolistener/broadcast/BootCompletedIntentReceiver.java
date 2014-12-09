package com.android2ee.audiolistener.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android2ee.audiolistener.mysms.service.MyService;

public class BootCompletedIntentReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
			   Intent myService = new Intent(context, MyService.class);
			   context.startService(myService);
		}
	}

}
