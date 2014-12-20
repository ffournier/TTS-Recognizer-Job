package com.example.audiolistener.job;

import java.util.ArrayList;

import com.android2ee.ttsjob.job.Job;

public class JobReceiveSMS extends Job {
	
	public static final String UTTERANCE_MESSAGE_SMS_RECEIVED = "com.android2ee.audiolistener.message_received";

	public JobReceiveSMS(String message) {
		// message getString(R.string.info_name, getNameinProgress()
		super(UTTERANCE_MESSAGE_SMS_RECEIVED, message,  true);
		ArrayList<String> listPositive = new ArrayList<String>();
		listPositive.add("oui");
		listPositive.add("ouais");
		ArrayList<String> listNegative = new ArrayList<String>();
		listNegative.add("non");
		setResults(listPositive, listNegative);
	}
	
	public JobReceiveSMS(String message, int retry) {
		// message getString(R.string.info_name, getNameinProgress()
		super(UTTERANCE_MESSAGE_SMS_RECEIVED, message,  true, retry);
		ArrayList<String> listPositive = new ArrayList<String>();
		listPositive.add("oui");
		listPositive.add("ouais");
		ArrayList<String> listNegative = new ArrayList<String>();
		listNegative.add("non");
		setResults(listPositive, listNegative);
	}

}
