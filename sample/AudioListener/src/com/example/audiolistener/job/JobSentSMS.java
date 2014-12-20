package com.example.audiolistener.job;

import com.android2ee.ttsjob.job.Job;

public class JobSentSMS extends Job {
	
	public static final String UTTERANCE_MESSAGE_SMS_SENT = "com.android2ee.audiolistener.message_sent";
	
	public JobSentSMS() {
		// message 
		super(UTTERANCE_MESSAGE_SMS_SENT, null,  false);
	}
	
}
