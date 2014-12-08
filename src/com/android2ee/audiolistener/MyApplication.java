package com.android2ee.audiolistener;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
	
	private boolean hasHeadSetBT;
	private boolean hasHeadSet;

	private static MyApplication instance;
	
	public static MyApplication getInstance() { return instance; }
	
	@Override
	public void onCreate() {
		super.onCreate();
		hasHeadSet = false;
		instance= this;
	}

	public static Context getContext() {
		return instance.getApplicationContext();
	}
	
	
	public void setHeadSetBT(boolean value) {
		hasHeadSetBT = value;
	}
	
	public void setHeadSet(boolean value) {
		hasHeadSet = value;
	}
	
	public boolean getHeadSetBT() {
		return hasHeadSetBT;
	}
	
	public boolean getHeadSet() {
		return hasHeadSet;
	}
}
