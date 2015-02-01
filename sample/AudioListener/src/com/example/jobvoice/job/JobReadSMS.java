package com.example.jobvoice.job;

import java.util.ArrayList;

import android.content.Context;

import com.android2ee.ttsjob.job.Job;
import com.example.jobvoice.R;

public class JobReadSMS extends Job {
	
	public static final String UTTERANCE_MESSAGE_SMS_TAKEN = "com.android2ee.jobvoice.message_taken";
	
	public JobReadSMS(Context context, String message, String name) {
		// message  getMessageinProgress() + ". Voulez vous envoyer un message à l'envoyeur ?"
		super(UTTERANCE_MESSAGE_SMS_TAKEN, context.getString(R.string.send_message, message, name),  true);
		ArrayList<String> listPositive = new ArrayList<String>();
		listPositive.add(context.getString(R.string.yes));
		listPositive.add(context.getString(R.string.yes2));
		ArrayList<String> listNegative = new ArrayList<String>();
		listNegative.add(context.getString(R.string.no));
		setResults(listPositive, listNegative);
	}
	
	public JobReadSMS(Context context, String message, String name, int retry) {
		// message  getMessageinProgress() + ". Voulez vous envoyer un message à l'envoyeur ?"
		super(UTTERANCE_MESSAGE_SMS_TAKEN, context.getString(R.string.send_message, message, name),  true, retry);
		ArrayList<String> listPositive = new ArrayList<String>();
		listPositive.add(context.getString(R.string.yes));
		listPositive.add(context.getString(R.string.yes2));
		ArrayList<String> listNegative = new ArrayList<String>();
		listNegative.add(context.getString(R.string.no));
		setResults(listPositive, listNegative);
	}

}
