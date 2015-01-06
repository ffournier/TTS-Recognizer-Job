package com.android2ee.ttsjob;

import android.app.Application;
import android.content.Context;

/**
 * Class Application
 * @author florian
 *
 */
public class TTSJobApplication extends Application {
	
	// variable
	private boolean hasHeadSetBT;
	private boolean hasHeadSet;

	// instance
	private static TTSJobApplication instance;
	
	/**
	 * Get Instance Singleton
	 * @return
	 */
	public static TTSJobApplication getInstance() { return instance; }
	
	@Override
	public void onCreate() {
		super.onCreate();
		hasHeadSet = false;
		instance= this;
	}

	/**
	 * Get Application Context
	 * @return
	 */
	public static Context getContext() {
		return instance.getApplicationContext();
	}
	
	/**
	 * Setter HeadSetBT
	 * @param value
	 */
	public void setHeadSetBT(boolean value) {
		hasHeadSetBT = value;
	}
	
	/**
	 * Setter HeadSet
	 * @param value
	 */
	public void setHeadSet(boolean value) {
		hasHeadSet = value;
	}
	
	/**
	 * Getter HeadSetBT
	 * @return
	 */
	public boolean getHeadSetBT() {
		return hasHeadSetBT;
	}
	
	/**
	 * Getter HeadSet
	 * @return
	 */
	public boolean getHeadSet() {
		return hasHeadSet;
	}
}
