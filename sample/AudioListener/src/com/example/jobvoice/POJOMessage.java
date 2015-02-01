package com.example.jobvoice;

import com.android2ee.ttsjob.service.POJOObject;

public class POJOMessage extends POJOObject {
	
	public String message;
	public String phoneNumber;
	public String name;
	
	public static final String KEY_SMS = "treatSMS";
	public static final String KEY_INCOMINGCALL = "treatCall";
	
	
	public POJOMessage(String key, String message, String phoneNumber, String name) {
		super(key);
		this.message = message;
		this.phoneNumber = phoneNumber;
		this.name = name;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public String getValidateName() {
		String result = name;
		if (result == null || result.length() == 0) {
			result = phoneNumber;
		}
		return result;
	}
	
	public static boolean isSMSType(POJOObject object) {
		return isType(object, KEY_SMS);
	}
	
	public static boolean isInComingCallType(POJOObject object) {
		return isType(object, KEY_INCOMINGCALL);
	}
	
}
