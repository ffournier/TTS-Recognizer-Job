package com.example.audiolistener.job;

import java.util.ArrayList;

import com.android2ee.ttsjob.job.Job;

public class JobReadSMS extends Job {
	
	public static final String UTTERANCE_MESSAGE_SMS_TAKEN = "com.android2ee.audiolistener.message_taken";
	
	public JobReadSMS(String message) {
		// message  getMessageinProgress() + ". Voulez vous envoyer un message à l'envoyeur ?"
		super(UTTERANCE_MESSAGE_SMS_TAKEN, message,  true);
		ArrayList<String> listPositive = new ArrayList<String>();
		listPositive.add("oui");
		listPositive.add("ouais");
		ArrayList<String> listNegative = new ArrayList<String>();
		listNegative.add("non");
		setResults(listPositive, listNegative);
	}

}
