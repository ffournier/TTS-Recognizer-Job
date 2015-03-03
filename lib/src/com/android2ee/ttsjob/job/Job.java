package com.android2ee.ttsjob.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.speech.tts.TextToSpeech;
import android.util.Log;

/**
 * Class Job
 * @author florian
 *
 */
public abstract class Job {
	
	// variable
	// id of the job
	String id;
	// matches 
	ArrayList<String> matchesPositive;
	ArrayList<String> matchesNegative;
	
	// other matches
	HashMap<Integer, List<String>> matchesOther;
	
	// has recognizer and tts message
	boolean hasRecognizer;
	String messageTTS;
	// retry
	int retry;
	int maxRetry;
	
	static final int ERROR_MATCH_NOT_FOUND = 0;
	static final int ERROR_RECOGNIZER = 1;
	
	// map save all son Job
	HashMap<Integer, Job> sonJob = new HashMap<Integer, Job>();
	
	/**
	 * Constructor
	 * @param id
	 * @param messageTTS
	 * @param hasRecognizer
	 */
	public Job(String id, String messageTTS, boolean hasRecognizer) {
		this.id = id;
		this.matchesPositive = null;
		this.matchesNegative = null;
		this.messageTTS = messageTTS;
		this.hasRecognizer = hasRecognizer;
		this.maxRetry = 0;
		this.retry = 0;
		this.matchesOther = new HashMap<>();
	}
	
	/**
	 * Constructor
	 * @param id
	 * @param messageTTS
	 * @param hasRecognizer
	 * @param maxRetry
	 */
	public Job(String id, String messageTTS, boolean hasRecognizer, int maxRetry) {
		this(id, messageTTS, hasRecognizer);
		this.maxRetry = maxRetry;
		this.matchesOther = new HashMap<>();
	}
	
	/**
	 * Set results possible for this job 
	 * @param resultsPositive : positive answer
	 * @param resultsNegative : negative answer
	 */
	protected void setResults(ArrayList<String> resultsPositive, ArrayList<String> resultsNegative) {
		matchesPositive = resultsPositive;
		matchesNegative = resultsNegative;
	}
	
	/**
	 * Set results possible for this job 
	 * @param codeResult : code associate to this search
	 * @param resultsOthers : the list string to search
	 */
	protected void addResults(Integer codeResult, ArrayList<String> resultsOthers) throws IllegalArgumentException {
		
		if (codeResult >= JobAnswer.EMPTY && codeResult <= JobAnswer.MAX) {
			throw new IllegalArgumentException("codeResults exists already in JobAnswer");
		}
		matchesOther.put(codeResult, resultsOthers);
	}
	
	/**
	 * get Id of this job
	 * @return
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * get if this job has a recognizer 
	 * @return
	 */
	public boolean hasRecognizer() {
		return hasRecognizer;
	}
	
	/**
	 * Set message read
	 * @param message
	 */
	public void setMessageTTS(String message) {
		messageTTS = message;
	}
	
	/**
	 * Get message read
	 * @return
	 */
	public String getMessageTTS() {
		return messageTTS;
	}
	
	/**
	 * Set the id in map given, this id is to found wich text is read by TTS
	 * @param map
	 * @return
	 */
	public HashMap<String, String> startTTS(HashMap<String, String> map) {
		map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, id);
		return map;
	}
	
	/**
	 * Test text listen by the recognizer 
	 * TODO we can add a map of matches result with an answer associate ?
	 * @param voiceResults
	 * @return the answer found, in function of negative and positive answer
	 */
	public Integer onResult(ArrayList<String> voiceResults) {
		Integer result = JobAnswer.NOT_FOUND;
		
		if (voiceResults != null && voiceResults.size() > 0) {
			boolean notFound = true;
			for (int i = 0; i < voiceResults.size() && notFound; i++) {
				String match = voiceResults.get(i);
				// TODO maybe evolve search ...
				if (matchesPositive != null && matchesPositive.contains(match)){
					result = JobAnswer.POSITIVE_ANSWER;
					notFound = false;
				} else if (matchesNegative != null && matchesNegative.contains(match)){
					result = JobAnswer.NEGATIVE_ANSWER;
					notFound = false;
				} else if (matchesOther.size() > 0 ) {
					Iterator<Entry<Integer,List<String>>> it = matchesOther.entrySet().iterator();
				    while (it.hasNext() && notFound) {
				        Map.Entry<Integer, List<String>> pair = it.next();
				        List<String> list = pair.getValue();
				        if (list != null && list.contains(match)) {
				        	result = pair.getKey();
				        	notFound = false;
				        }
				    }
				}
			}	
		} else {
			result = JobAnswer.EMPTY;
		}
		
		return result;
	}
	
	
	/**
	 * test if a son Job has this key
	 * @param key
	 * @return
	 */
	public boolean hasKey(Integer key) {
		return getJobByKey(key) != null;
	}
	
	/**
	 * add son job 
	 * @param key
	 * @param job
	 * @return
	 */
	public boolean addSonJob(Integer key, Job job ) {
		if (sonJob != null && job != null) {
			if (!hasKey(key)) {
				sonJob.put(key, job);
				return true;
			} 
		}
		return false;
	}
	
	/**
	 * get the Job in son Job in function of this key
	 * @param key
	 * @return
	 */
	public Job getJobByKey(Integer key) {
		if (sonJob != null) {
			return sonJob.get(key);
		}
		return null;
	}
	
	/**
	 * Get Job in sonJob in depends if the id of job
	 * @param id
	 * @return
	 */
	protected boolean hasJob(String id) {
		return getJob(id) != null;
	}
	
	/**
	 * Get Job in sonJob in function of the id
	 * @param id
	 * @return
	 */
	protected Job getJob(String id) {
		if (sonJob != null) {
			Job obj;
			Iterator<Entry<Integer, Job>> it = sonJob.entrySet().iterator();
		    while (it.hasNext()) {
		        Entry<Integer, Job> pairs = it.next();
		        obj = (Job) pairs.getValue();
		        if (obj != null && obj.getId() != null && obj.getId().equalsIgnoreCase(id)) {
					return obj;
				}
		    }
		}
	    return null;
	}
	
	/**
	 * Add a new retry
	 */
	public void addRetry() {
		this.retry++;
		Log.i(getClass().getCanonicalName(), "One Retry " + this.retry);
	}
	
	/**
	 * Test if this job can retry once time more 
	 * @return
	 */
	public boolean canRetry() {
		Log.i(getClass().getCanonicalName(), "canRetry " + (retry < maxRetry));
		return retry < maxRetry;
	}
	
	/**
	 * reset Retry
	 */
	public void resetRetry() {
		this.retry = 0;
	}
	
}
