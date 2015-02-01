package com.example.jobvoice;


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
import com.example.jobvoice.job.JobEndSMS;
import com.example.jobvoice.job.JobInComingCall;
import com.example.jobvoice.job.JobReadSMS;
import com.example.jobvoice.job.JobReceiveSMS;
import com.example.jobvoice.job.JobSendSMS;
import com.example.jobvoice.job.JobSentSMS;

public class MyService extends TTSJobService {
	
	public static final String KEY_PHONE_NUMBER = "com.android2ee.jobvoice.phone_number";
	
	public static final String KEY_SMS = "SMS";
	public static final String KEY_INCOMINGCALL = "INCOMINGCALL";
	
	
	private final static int MAX_RETRY = 2;
	
	private String getContact(String phoneNumber) {
		Log.i(getClass().getCanonicalName(), "getContact");
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
		Log.i(getClass().getCanonicalName(), "getMetaData");
		if (bundle != null && bundle.containsKey(KEY_NAME)) {
			String key = bundle.getString(KEY_NAME);
			if (key.equalsIgnoreCase(KEY_SMS)) {
				String message = bundle.getString(KEY_MESSAGE);
				String phoneNumber = bundle.getString(KEY_PHONE_NUMBER);
				String name = getContact(phoneNumber);
				Log.i(getClass().getCanonicalName(), "getMetaData Message " + phoneNumber + "   " + name);
				return new POJOMessage(POJOMessage.KEY_SMS, message, phoneNumber, name);
			} else if (key.equalsIgnoreCase(KEY_INCOMINGCALL)) {
				String phoneNumber = bundle.getString(KEY_PHONE_NUMBER);
				String name = getContact(phoneNumber);
				Log.i(getClass().getCanonicalName(), "getMetaData InComingCall " + phoneNumber + "   " + name);
				return new POJOMessage(POJOMessage.KEY_INCOMINGCALL, "", phoneNumber, name);
			}
		}
		return null;
	}

	@Override
	protected Jobs addJobs(POJOObject object) {
		Log.i(getClass().getCanonicalName(), "addJobs");
		Jobs jobs = null;
		if (POJOMessage.isSMSType(object)) {
			POJOMessage message = (POJOMessage) object;
			jobs = new Jobs();
			JobReceiveSMS jobReceiveSMS = new JobReceiveSMS(this, message.getValidateName(), MAX_RETRY);
			JobReadSMS jobReadSMS = new JobReadSMS(this, message.getMessage(),message.getValidateName(), MAX_RETRY);
			JobSendSMS jobSendSMS = new JobSendSMS(this, message.getPhoneNumber(), MAX_RETRY);
			JobSentSMS jobSentSMS = new JobSentSMS();
			jobSendSMS.addSonJob(JobAnswer.NOT_FOUND, jobSentSMS);
			jobReadSMS.addSonJob(JobAnswer.POSITIVE_ANSWER, jobSendSMS);
			jobReceiveSMS.addSonJob(JobAnswer.POSITIVE_ANSWER, jobReadSMS);
			jobs.addJob(jobReceiveSMS);
			JobEndSMS jobEnd = new JobEndSMS(this);
			jobs.addJob(jobEnd);
		} else if (POJOMessage.isInComingCallType(object)) {
			POJOMessage message = (POJOMessage) object;
			jobs = new Jobs();
			JobSendSMS jobSendSMS = new JobSendSMS(this, message.getPhoneNumber(), MAX_RETRY);
			JobSentSMS jobSentSMS = new JobSentSMS();
			jobSendSMS.addSonJob(JobAnswer.NOT_FOUND, jobSentSMS);
			JobInComingCall jobInCommingCall = new JobInComingCall(this, message.getValidateName(),  message.getPhoneNumber(), MAX_RETRY);
			jobInCommingCall.addSonJob(JobAnswer.NEGATIVE_ANSWER, jobSendSMS);
			jobs.addJob(jobInCommingCall);
		}
		return jobs;
	}

	@Override
	protected boolean isBluetooth() {
		return true;
	}

	@Override
	protected Boolean isPreferenceLanguage() {
		return null;
	}

	@Override
	protected Long getTimeAfterStop() {
		return (long) (15 * 1000);
	}

}
