package com.android2ee.audiolistener.service;

public class POJOMessage {
	
	public String message;
	public String phoneNumber;
	public String name;
	
	
	public POJOMessage(String message, String phoneNumber, String name) {
		super();
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
	
}
