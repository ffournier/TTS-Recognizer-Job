package com.example.jobvoice.job;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.android2ee.ttsjob.job.Job;
import com.android2ee.ttsjob.job.JobAnswer;
import com.example.jobvoice.R;

public class JobInComingCall extends Job {
	
	public static final String UTTERANCE_MESSAGE_INCOMINGCALL = "com.android2ee.jobvoice.incomingcall";
	
	Context context;
	String phoneNumber;

	public JobInComingCall(Context context, String name, String phoneNumber) {
		// message getString(R.string.info_name, getNameinProgress()
		super(UTTERANCE_MESSAGE_INCOMINGCALL, context.getString(R.string.init_call, name),  true);
		this.context = context;
		this.phoneNumber = phoneNumber;
		ArrayList<String> listPositive = new ArrayList<String>();
		listPositive.add("oui");
		listPositive.add("ouais");
		ArrayList<String> listNegative = new ArrayList<String>();
		listNegative.add("non");
		setResults(listPositive, listNegative);
	}
	
	public JobInComingCall(Context context, String name, String phoneNumber, int retry) {
		// message getString(R.string.info_name, getNameinProgress()
		super(UTTERANCE_MESSAGE_INCOMINGCALL, context.getString(R.string.init_call, name),  true, retry);
		this.context = context;
		this.phoneNumber = phoneNumber;
		ArrayList<String> listPositive = new ArrayList<String>();
		listPositive.add("oui");
		listPositive.add("ouais");
		ArrayList<String> listNegative = new ArrayList<String>();
		listNegative.add("non");
		setResults(listPositive, listNegative);
	}
	
	@Override
	public Integer onResult(ArrayList<String> voiceResults) {
		Integer answer = super.onResult(voiceResults);
		if (answer !=  JobAnswer.POSITIVE_ANSWER) {
			// call
			 String uri = "tel:" + phoneNumber.trim() ;
			 Intent intent = new Intent(Intent.ACTION_CALL);
			 intent.setData(Uri.parse(uri));
			 context.startActivity(intent);
			 return answer;
		}
		return answer;
	}

}
