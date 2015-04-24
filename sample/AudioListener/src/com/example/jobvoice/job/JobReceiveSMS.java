package com.example.jobvoice.job;

import java.util.ArrayList;

import android.content.Context;

import com.android2ee.ttsjob.job.Job;
import com.example.jobvoice.R;

public class JobReceiveSMS extends Job {
	
	public static final int FUCK_ANSWER = 10;
	public static final int LATER_ANSWER = 11;
	public static final String UTTERANCE_MESSAGE_SMS_RECEIVED = "com.android2ee.jobvoice.message_received";
	
	public JobReceiveSMS(Context context, String name) {
		// message getString(R.string.info_name, getNameinProgress()
		super(UTTERANCE_MESSAGE_SMS_RECEIVED, context.getString(R.string.info_name, name),  true);
		ArrayList<String> listPositive = new ArrayList<String>();
		listPositive.add(context.getString(R.string.yes));
		listPositive.add(context.getString(R.string.yes2));
		ArrayList<String> listNegative = new ArrayList<String>();
		listNegative.add(context.getString(R.string.no));
		setResults(listPositive, listNegative);
		myList other = new myList();
		other.add(context.getString(R.string.shut_up));
		addResults(FUCK_ANSWER, other);
		myList otherLater = new myList();
		otherLater.add(context.getString(R.string.later));
		addResults(LATER_ANSWER, otherLater);
	}
	
	public JobReceiveSMS(Context context, String name, int retry) {
		// message getString(R.string.info_name, getNameinProgress()
		super(UTTERANCE_MESSAGE_SMS_RECEIVED, context.getString(R.string.info_name, name),  true, retry);
		ArrayList<String> listPositive = new ArrayList<String>();
		listPositive.add(context.getString(R.string.yes));
		listPositive.add(context.getString(R.string.yes2));
		ArrayList<String> listNegative = new ArrayList<String>();
		listNegative.add(context.getString(R.string.no));
		setResults(listPositive, listNegative);
		myList other = new myList();
		other.add(context.getString(R.string.shut_up));
		addResults(FUCK_ANSWER, other);
		myList otherLater = new myList();
		otherLater.add(context.getString(R.string.later));
		addResults(LATER_ANSWER, otherLater);
	}
	
	public JobReceiveSMS(Context context, String name, int retry, long time) {
		// message getString(R.string.info_name, getNameinProgress()
		super(UTTERANCE_MESSAGE_SMS_RECEIVED, context.getString(R.string.info_name, name),  true, retry, time);
		ArrayList<String> listPositive = new ArrayList<String>();
		listPositive.add(context.getString(R.string.yes));
		listPositive.add(context.getString(R.string.yes2));
		ArrayList<String> listNegative = new ArrayList<String>();
		listNegative.add(context.getString(R.string.no));
		setResults(listPositive, listNegative);
		myList other = new myList();
		other.add(context.getString(R.string.shut_up));
		addResults(FUCK_ANSWER, other);
		myList otherLater = new myList();
		otherLater.add(context.getString(R.string.later));
		addResults(LATER_ANSWER, otherLater);
	}

}
