package com.android2ee.ttsjob.job;

import java.util.ArrayList;

/**
 * Class which contains all jobs
 * @author florian
 *
 */
public class Jobs {
	
	ArrayList<Job> jobs = new ArrayList<Job>();
	
	public static final int NOK = 1;
	public static final int OK = 0;
	public static final int ERROR = -1;
	
	/**
	 * Add a new Job
	 * @param job
	 */
	public void addJob(Job job) {
		jobs.add(job);
	}
	
	/**
	 * Remove a job
	 * @param job
	 */
	public void removeJob(Job job) {
		jobs.remove(job);
	}
	
	/*public void removeJob(String id) {
		Job jobFound = findJob(id);
		if (jobFound != null) {
			removeJob(jobFound);
		}
	}*/
	
	/**
	 * Find Job in First Step
	 * @param id
	 * @return
	 */
	/*public Job findJob(String id) {
		Job jobFound = null;
		for (Job job : jobs) {
			if (job.getId().equalsIgnoreCase(id)) {
				jobFound = job;
				break;
			}
		}
		return jobFound;
	}*/
	
	/**
	 * Get the first Job
	 * @return
	 */
	public Job getFirstJob() {
		if (jobs != null && jobs.size() > 0) {
			return jobs.get(0);
		} else {
			return null;
		}
	}
	
	/**
	 * Get next Job in list or the son of the current in function of the answer
	 * @param currentJob
	 * @param answer
	 * @return
	 */
	public Job getNextJob(Job currentJob, Integer answer) {
		Job job = getNextSonJob(currentJob, answer);
		if (job == null) {
			if (currentJob != null && answer == JobAnswer.EMPTY && 
					currentJob.canRetry()) {
				currentJob.addRetry();
				return currentJob;
			}
			job = getNextJobInList();
		}
		return job;
	}
	
	/**
	 * Get Next Son Job in function of the answer
	 * @param currentJob
	 * @param answer
	 * @return
	 */
	private Job getNextSonJob(Job currentJob, Integer answer) {
		if (currentJob != null) {
			if (currentJob.hasKey(answer)){
				return currentJob.getJobByKey(answer);
			}
		}
		return null;
	}
	
	/**
	 * Get the next job in list and remove the current
	 * @return
	 */
	public Job getNextJobInList() {
		if (jobs != null && jobs.size() > 0) {
			jobs.remove(0);
			if (jobs.size() > 0) {
				return jobs.get(0);
			}
		}
		return null;
	}
	
	/**
	 * Remove all Job
	 */
	public void removeAll() {
		if (jobs != null) {
			jobs.clear();
		}
	}
	
	/**
	 * Test if we have a job was running 
	 * @return
	 */
	public boolean isRunning() {
		return jobs != null && jobs.size() > 0;
	}
}
