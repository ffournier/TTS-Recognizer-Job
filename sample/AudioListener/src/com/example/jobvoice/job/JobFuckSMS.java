package com.example.jobvoice.job;

import com.android2ee.ttsjob.job.Job;

public class JobFuckSMS extends Job {
	
	public static final String UTTERANCE_MESSAGE_SMS_FUCK = "com.android2ee.jobvoice.message_fuck";
	
	public JobFuckSMS() {
		// message 
		super(UTTERANCE_MESSAGE_SMS_FUCK, "Ta gueule toi même",  false);
	}
}
