package com.example.audiolistener;


import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;

import com.android2ee.ttsjob.job.JobAnswer;
import com.android2ee.ttsjob.job.Jobs;
import com.android2ee.ttsjob.service.POJOObject;
import com.android2ee.ttsjob.service.TTSJobService;
import com.example.audiolistener.job.JobReadSMS;
import com.example.audiolistener.job.JobReceiveSMS;
import com.example.audiolistener.job.JobSendSMS;
import com.example.audiolistener.job.JobSentSMS;

public class MyService extends TTSJobService {
	
	private String getContact(String phoneNumber) {
		Log.e("SmsReceiver", "getContact");
		ContentResolver cr = getContentResolver();
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		Cursor cursor = cr.query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);
	    if (cursor == null) {
	        return phoneNumber;
	    }
	    String contactName = null;
	    if(cursor.moveToFirst()) {
	        contactName = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));
	    }

	    if(cursor != null && !cursor.isClosed()) {
	        cursor.close();
	    }

	    return contactName != null ? contactName : phoneNumber;
	}

	@Override
	protected POJOObject getMetaData(Bundle bundle) {
		Log.e("SmsReceiver", "getMetaData");
		if (bundle != null) {
			if ( bundle.containsKey(KEY_MESSAGE)) {
				String message = bundle.getString(KEY_MESSAGE);
				String phoneNumber = bundle.getString(KEY_NAME);
				String name = getContact(phoneNumber);
				Log.e("SmsReceiver", "getMetaData Message " + phoneNumber + "   " + name);
				return new POJOMessage(message, phoneNumber, name);
			}
		}
		return null;
	}

	@Override
	protected Jobs addJobs(POJOObject object) {
		Log.e("SmsReceiver", "addJobs");
		Jobs jobs = null;
		if (POJOMessage.isSMSType(object)) {
			POJOMessage message = (POJOMessage) object;
			jobs = new Jobs();
			JobReceiveSMS jobReceiveSMS = new JobReceiveSMS(getString(R.string.info_name, message.getValidateName()));
			JobReadSMS jobReadSMS = new JobReadSMS(message.getMessage() + ". Voulez vous envoyer un message à " +  message.getValidateName() + " ?");
			JobSendSMS jobSendSMS = new JobSendSMS(message.getPhoneNumber());
			JobSentSMS jobSentSMS = new JobSentSMS();
			jobSendSMS.addSonJob(JobAnswer.NOT_FOUND, jobSentSMS);
			jobReadSMS.addSonJob(JobAnswer.POSITIVE_ANSWER, jobSendSMS);
			jobReceiveSMS.addSonJob(JobAnswer.POSITIVE_ANSWER, jobReadSMS);
			jobs.addJob(jobReceiveSMS);
		}
		return jobs;
	}

}
