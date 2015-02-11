package com.example.jobvoice;

import android.content.Context;
import android.content.Intent;

import com.android2ee.ttsjob.broadcast.TTSJobBootCompletedIntentReceiver;

public class MyBootCompletedIntentReceiver extends TTSJobBootCompletedIntentReceiver {
	
	

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
			   context.startService(new Intent(context,NotificationServiceListener.class));
		}
	}

	protected Intent callService(Context context) {
		return new Intent(context, MyService.class);
	}

}
