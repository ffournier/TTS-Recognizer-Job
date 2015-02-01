package com.example.jobvoice;

import android.content.Context;
import android.content.Intent;

import com.android2ee.ttsjob.broadcast.TTSJobBootCompletedIntentReceiver;

public class MyBootCompletedIntentReceiver extends TTSJobBootCompletedIntentReceiver {

	protected Intent callService(Context context) {
		return new Intent(context, MyService.class);
	}

}
