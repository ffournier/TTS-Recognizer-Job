package com.android2ee.audiolistener.job;

import java.util.ArrayList;
import java.util.HashMap;

import android.speech.tts.TextToSpeech;

public abstract class Job {
	
	String id;
	ArrayList<String> matchesPositive;
	ArrayList<String> matchesNegative;
	boolean hasRecognizer;
	String messageTTS;
	
	static final int ERROR_MATCH_NOT_FOUND = 0;
	static final int ERROR_RECOGNIZER = 1;
	
	
	public static final int NOT_FOUND = 0;
	public static final int POSITIVE_ANSWER = 1;
	public static final int NEGATIVE_ANSWER = 2;
	

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
	
	public int onResult(ArrayList<String> voiceResults) {
		int result = NOT_FOUND;
		for (String match : voiceResults) {
			// TODO maybe evolve search ...
			if (matchesPositive != null && matchesPositive.contains(match)){
				result = POSITIVE_ANSWER;
				break;
			}
			if (matchesNegative != null && matchesNegative.contains(match)){
				result = NEGATIVE_ANSWER;
				break;
			}
		}	
		return result;
	}
	
}
