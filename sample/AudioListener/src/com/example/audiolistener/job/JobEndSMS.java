package com.example.audiolistener.job;

import com.android2ee.ttsjob.job.Job;

public class JobEndSMS extends Job {
	
	public static final String UTTERANCE_MESSAGE_SMS_END = "com.android2ee.audiolistener.message_end";
	
	public JobEndSMS() {
		// message 
		super(UTTERANCE_MESSAGE_SMS_END, "Opération terminée. merci !",  false);
	}
	
}
