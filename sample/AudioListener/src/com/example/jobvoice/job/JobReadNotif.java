package com.example.jobvoice.job;

import android.content.Context;

import com.android2ee.ttsjob.job.Job;

public class JobReadNotif extends Job {
	
	public static final String UTTERANCE_MESSAGE_NOTIF_READ = "com.android2ee.jobvoice.notif_read";
	
	public JobReadNotif(Context context, String message) {
		// message  getMessageinProgress() + ". Voulez vous envoyer un message à l'envoyeur ?"
		super(UTTERANCE_MESSAGE_NOTIF_READ, message,  false);
	}
	
	public JobReadNotif(Context context, String message, int retry) {
		// message  getMessageinProgress() + ". Voulez vous envoyer un message à l'envoyeur ?"
		super(UTTERANCE_MESSAGE_NOTIF_READ, message,  false, retry);
	}

}
