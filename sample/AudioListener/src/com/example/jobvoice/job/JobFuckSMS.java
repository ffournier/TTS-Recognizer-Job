package com.example.jobvoice.job;

import android.content.Context;


import com.android2ee.ttsjob.job.Job;
import com.example.jobvoice.R;

public class JobFuckSMS extends Job {
	
	public static final String UTTERANCE_MESSAGE_SMS_FUCK = "com.android2ee.jobvoice.message_fuck";
	
	public JobFuckSMS(Context context) {
		// message 
		super(UTTERANCE_MESSAGE_SMS_FUCK, context.getString(R.string.shutup_yourself),  false);
	}
}
