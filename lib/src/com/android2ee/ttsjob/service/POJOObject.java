package com.android2ee.ttsjob.service;

import com.android2ee.ttsjob.job.Jobs;

/**
 * abstract class to get information to create jobs to start
 * @author florian
 *
 */
public abstract class POJOObject {
	
	// type of Object
	String value;
	Jobs jobs;
	
	/**
	 * Constructor
	 * @param value
	 */
	public POJOObject(String value) {
		super();
		this.value = value;
	}
	
	/**
	 * Constructor
	 * @param value
	 */
	public POJOObject(Jobs jobs) {
		super();
		this.jobs = jobs;
	}

	public String getValue() {
		return value;
	}
	
	/**
	 * Test if a job is already implemented
	 * @return
	 */
	public boolean hasJob() {
		return jobs != null;
	}
	
	/**
	 * Get jobs
	 * @return
	 */
	public Jobs getJobs() {
		return jobs;
	}
	
	protected static boolean isType(POJOObject object, String value) {
		if (object.getValue() != null) {
			return (object.getValue().equalsIgnoreCase(value));
		} 
		return false;
	}
}
