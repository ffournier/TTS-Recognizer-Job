package com.android2ee.audiolistener;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

	private static MyApplication instance;
	
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		instance= this;
	}



	public static Context getContext() {
		return instance.getApplicationContext();
	}
}
