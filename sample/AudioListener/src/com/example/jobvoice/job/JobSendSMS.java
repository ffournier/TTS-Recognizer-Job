package com.example.jobvoice.job;

import java.util.ArrayList;

import android.content.Context;
import android.telephony.SmsManager;

import com.android2ee.ttsjob.job.Job;
import com.android2ee.ttsjob.job.JobAnswer;
import com.example.jobvoice.R;

public class JobSendSMS extends Job {
	
	public static final String UTTERANCE_MESSAGE_SMS_SEND = "com.android2ee.jobvoice.message_send";
	
	Context context;
	String phoneNumber;
	
	public JobSendSMS(Context context, String phoneNumber) {
		// message "Annoncez le message ?"
		super(UTTERANCE_MESSAGE_SMS_SEND, context.getString(R.string.say_message),  true);
		this.context = context;
		this.phoneNumber = phoneNumber;
	}
	
	public JobSendSMS(Context context, String phoneNumber, int retry) {
		// message "Annoncez le message ?"
		super(UTTERANCE_MESSAGE_SMS_SEND, context.getString(R.string.say_message),  true, retry);
		this.context = context;
		this.phoneNumber = phoneNumber;
	}
	
	@Override
	public Integer onResult(ArrayList<String> voiceResults) {
		Integer answer = super.onResult(voiceResults);
		if (answer !=  JobAnswer.EMPTY) {
			sendSMSMessage(voiceResults.get(0));
			return answer;
		}
		return answer;
	}
	
	protected void sendSMSMessage(String message) {
	      try {
	         SmsManager smsManager = SmsManager.getDefault();
	         smsManager.sendTextMessage(phoneNumber, null, message, null, null);
	         // Message envoyé
	         updateJobSent(context.getString(R.string.sent_message, message));
	      } catch (Exception e) {
	         // Message Non envoyé
	    	  updateJobSent(context.getString(R.string.sent_no_message));
	      }
	}
	
	private void updateJobSent(String message) {
		if (hasJob(JobSentSMS.UTTERANCE_MESSAGE_SMS_SENT)) {
			Job job = getJob(JobSentSMS.UTTERANCE_MESSAGE_SMS_SENT);
			if (job != null) {
				job.setMessageTTS(message);
			}
		}
	}

}
