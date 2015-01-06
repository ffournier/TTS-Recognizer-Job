package com.android2ee.ttsjob.service;

/**
 * abstract class to get information to create jobs to start
 * @author florian
 *
 */
public abstract class POJOObject {
	
	// type of Object
	String value;
	
	/**
	 * Constructor
	 * @param value
	 */
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
