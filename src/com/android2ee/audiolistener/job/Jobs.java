package com.android2ee.audiolistener.job;

import java.util.ArrayList;

public class Jobs {
	
	ArrayList<Job> jobs = new ArrayList<Job>();
	
	public static final int NOK = 1;
	public static final int OK = 0;
	public static final int ERROR = -1;
	
	public void addJob(Job job) {
		jobs.add(job);
	}
	
	public void removeJob(Job job) {
		jobs.remove(job);
	}
	
	public void removeJob(String id) {
		Job jobFound = findJob(id);
		if (jobFound != null) {
			removeJob(jobFound);
		}
	}
	
	public Job findJob(String id) {
		Job jobFound = null;
		for (Job job : jobs) {
			if (job.getId().equalsIgnoreCase(id)) {
				jobFound = job;
				break;
			}
		}
		return jobFound;
	}
	
	public Job getFirstJob() {
		if (jobs != null && jobs.size() > 0) {
			return jobs.get(0);
		} else {
			return null;
		}
	}
	
	public Job getNextJob(Job currentJob) {
		if (jobs != null && jobs.size() > 0) {
			Job job = jobs.get(0);
			if (job instanceof JobRebounds) {
				JobRebounds jobRebounds = (JobRebounds) job;
				// we test if the next job is the current Job
				job = jobRebounds.getNextJob(currentJob);
				if (job != null) {
					return job;	
				}
			}
			
			jobs.remove(0);
			if (jobs.size() > 0) {
				return jobs.get(0);
			}
		}
		return null;
	}
	
	public void removeAll() {
		if (jobs != null) {
			jobs.clear();
		}
	}
	
	public boolean isRunning() {
		return jobs != null && jobs.size() > 0;
	}
}
