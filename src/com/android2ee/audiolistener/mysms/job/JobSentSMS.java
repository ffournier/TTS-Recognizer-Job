package com.android2ee.audiolistener.mysms.job;

import com.android2ee.audiolistener.job.Job;

public class JobSentSMS extends Job {
	
	public static final String UTTERANCE_MESSAGE_SMS_SENT = "com.android2ee.audiolistener.message_sent";
	
	public JobSentSMS() {
		// message 
		super(UTTERANCE_MESSAGE_SMS_SENT, null,  false);
	}
	
	public void setMessage(String message) {
		
	}
	
}
