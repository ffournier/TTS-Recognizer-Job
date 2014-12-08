package com.android2ee.audiolistener.job;


public abstract class JobRebounds extends Job {
	
	protected Job jobRebounds;

	public JobRebounds(String id, String messageTTS, boolean hasRecognizer, Job jobRebounds) {
		super(id, messageTTS, hasRecognizer);
		this.jobRebounds = jobRebounds;
	}
	
	public boolean hasNextJob(){ return jobRebounds!=null;};
	
	public Job getNextJob() { 
		return jobRebounds;
	};
	
	public Job getNextJob(Job currentJob) { 
		if (currentJob == this && hasNextJob()) {
			return jobRebounds;
		} else {
			if (jobRebounds instanceof JobRebounds) {
				JobRebounds rebounds = (JobRebounds) jobRebounds;
				if (rebounds.hasNextJob() && rebounds.getNextJob(currentJob) != null) {
					return rebounds.getNextJob(currentJob);
				}
			}
		}
		return null;
	};
	
}
