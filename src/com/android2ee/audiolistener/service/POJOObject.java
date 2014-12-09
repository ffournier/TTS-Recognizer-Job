package com.android2ee.audiolistener.service;

public class POJOObject {
	
	String value;
	
	public POJOObject(String value) {
		super();
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
	protected static boolean isType(POJOObject object, String value) {
		return (object.getValue().equalsIgnoreCase(value));
	}
}
