package com.android2ee.audiolistener.service;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.PhoneLookup;

import com.android2ee.audiolistener.R;
import com.android2ee.audiolistener.activity.MainActivity.MyPreferences;
import com.android2ee.audiolistener.job.Jobs;
import com.android2ee.audiolistener.job.list.JobReadSMS;
import com.android2ee.audiolistener.job.list.JobReceiveSMS;
import com.android2ee.audiolistener.job.list.JobSendSMS;
import com.android2ee.audiolistener.job.list.JobSentSMS;

public class MyService extends JobService {
	
	private String getContact(String phoneNumber) {
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
		if (bundle != null) {
			if ( bundle.containsKey(KEY_MESSAGE)) {
				String message = bundle.getString(KEY_MESSAGE);
				String phoneNumber = bundle.getString(KEY_NAME);
				String name = getContact(phoneNumber);
				return new POJOMessage(message, phoneNumber, name);
			}
		}
		return null;
	}

	@Override
	protected Jobs addJobs(POJOObject object) {
		Jobs jobs = null;
		if (POJOMessage.isSMSType(object)) {
			POJOMessage message = (POJOMessage) object;
			jobs = new Jobs();
			jobs.addJob(new JobReceiveSMS(getString(R.string.info_name, message.getValidateName())));
			jobs.addJob(new JobReadSMS(message.getMessage() + ". Voulez vous envoyer un message à l'envoyeur ?"));
			JobSentSMS jobSentSMS = new JobSentSMS();
			jobs.addJob(new JobSendSMS(message.getPhoneNumber(), jobSentSMS));
		}
		return jobs;
	}

}
