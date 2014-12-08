package com.android2ee.audiolistener.job.list;

import java.util.ArrayList;

import android.telephony.SmsManager;

import com.android2ee.audiolistener.job.Job;
import com.android2ee.audiolistener.job.JobRebounds;

public class JobSendSMS extends JobRebounds {
	
	private static final String UTTERANCE_MESSAGE_SMS_SEND = "com.android2ee.audiolistener.message_send";
	
	String phoneNumber;
	
	public JobSendSMS(String phoneNumber, Job jobRebounds) {
		// message "Annoncez le message ?"
		super(UTTERANCE_MESSAGE_SMS_SEND, "Annoncez le message ?",  true, jobRebounds);
		this.phoneNumber = phoneNumber;
	}
	
	@Override
	public int onResult(ArrayList<String> voiceResults) {
		if (voiceResults!=null && voiceResults.size() > 0) {
			sendSMSMessage(voiceResults.get(0));
			return Job.POSITIVE_ANSWER;
		} else {
			return Job.NEGATIVE_ANSWER;
		}
	}
	
	protected void sendSMSMessage(String message) {
	      try {
	         SmsManager smsManager = SmsManager.getDefault();
	         smsManager.sendTextMessage(phoneNumber, null, message, null, null);
	         // Message envoyé
	         updateJob(message + ". Message envoyé");
	      } catch (Exception e) {
	         // Message Non envoyé
	    	 updateJob("Message non envoyé");
	      }
	}
	
	private void updateJob(String message) {
		if (jobRebounds != null) {
			jobRebounds.setMessageTTS(message);
		}
	}

}
