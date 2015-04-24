package com.android2ee.ttsjob.service;

import com.android2ee.ttsjob.job.Jobs;

/**
 * abstract class to get information to create jobs to start
 * @author florian
 *
 */
public class POJOJobs extends POJOObject {
	
	
	/**
	 * Constructor
	 * @param value
	 */
	public POJOJobs(Jobs jobs) {
		super(jobs);
	}

}
