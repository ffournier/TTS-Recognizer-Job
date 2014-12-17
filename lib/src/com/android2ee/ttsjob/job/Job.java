package com.android2ee.ttsjob.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.speech.tts.TextToSpeech;

public abstract class Job {
	
	String id;
	ArrayList<String> matchesPositive;
	ArrayList<String> matchesNegative;
	boolean hasRecognizer;
	String messageTTS;
	
	static final int ERROR_MATCH_NOT_FOUND = 0;
	static final int ERROR_RECOGNIZER = 1;
	
	HashMap<JobAnswer, Job> sonJob = new HashMap<JobAnswer, Job>();
	
	public Job(String id, String messageTTS, boolean hasRecognizer) {
		this.id = id;
		this.matchesPositive = null;
		this.matchesNegative = null;
		this.messageTTS = messageTTS;
		this.hasRecognizer = hasRecognizer;
	}
	
	protected void setResults(ArrayList<String> resultsPositive, ArrayList<String> resultsNegative) {
		matchesPositive = resultsPositive;
		matchesNegative =resultsNegative;
	}
	
	public String getId() {
		return id;
	}
	
	public boolean hasRecognizer() {
		return hasRecognizer;
	}
	
	public void setMessageTTS(String message) {
		messageTTS = message;
	}
	
	public String getMessageTTS() {
		return messageTTS;
	}
	
	public HashMap<String, String> startTTS(HashMap<String, String> map) {
		map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, id);
		return map;
	}
	
	public JobAnswer onResult(ArrayList<String> voiceResults) {
		JobAnswer result = JobAnswer.NOT_FOUND;
		
		if (voiceResults != null && voiceResults.size() > 0) {
			for (String match : voiceResults) {
				// TODO maybe evolve search ...
				if (matchesPositive != null && matchesPositive.contains(match)){
					result = JobAnswer.POSITIVE_ANSWER;
					break;
				}
				if (matchesNegative != null && matchesNegative.contains(match)){
					result = JobAnswer.NEGATIVE_ANSWER;
					break;
				}
			}	
		} else {
			result = JobAnswer.EMPTY;
		}
		
		return result;
	}
	
	
	public boolean hasKey(JobAnswer key) {
		return getJobByKey(key) != null;
	}
	
	public boolean addSonJob(JobAnswer key, Job job ) {
		if (sonJob != null && job != null) {
			if (!hasKey(key)) {
				sonJob.put(key, job);
				return true;
			} 
		}
		return false;
	}
	
	public Job getJobByKey(JobAnswer key) {
		if (sonJob != null) {
			return sonJob.get(key);
		}
		return null;
	}
	
	protected boolean hasJob(String id) {
		return getJob(id) != null;
	}
	
	protected Job getJob(String id) {
		if (sonJob != null) {
			Job obj;
			Iterator<Entry<JobAnswer, Job>> it = sonJob.entrySet().iterator();
		    while (it.hasNext()) {
		        Entry<JobAnswer, Job> pairs = it.next();
		        obj = (Job) pairs.getValue();
		        if (obj != null && obj.getId() != null && obj.getId().equalsIgnoreCase(id)) {
					return obj;
				}
		    }
		}
	    return null;
	}
	
}
