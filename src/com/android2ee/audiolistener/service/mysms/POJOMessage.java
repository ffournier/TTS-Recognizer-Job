package com.android2ee.audiolistener.service.mysms;

import com.android2ee.audiolistener.service.POJOObject;

public class POJOMessage extends POJOObject {
	
	public String message;
	public String phoneNumber;
	public String name;
	
	public static final String KEY_SMS = "treatSMS";
	
	
	public POJOMessage(String message, String phoneNumber, String name) {
		super(KEY_SMS);
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
	
}
