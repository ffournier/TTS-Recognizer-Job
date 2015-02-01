package com.example.jobvoice.job;

import android.content.Context;

import com.android2ee.ttsjob.job.Job;
import com.example.jobvoice.R;

public class JobEndSMS extends Job {
	
	public static final String UTTERANCE_MESSAGE_SMS_END = "com.android2ee.jobvoice.message_end";
	
	public JobEndSMS(Context context) {
		// message 
		super(UTTERANCE_MESSAGE_SMS_END, context.getString(R.string.end_message),  false);
	}
	
}
