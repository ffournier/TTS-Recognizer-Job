package com.example.jobvoice;

import java.util.List;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class NotificationServiceListener extends AccessibilityService {
	
	boolean isInit = false;
	
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
	    if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
	        //Do something, eg getting packagename
	        String pack = (String) event.getPackageName();
            if (pack.equalsIgnoreCase("com.google.android.calendar")) {
            	//The event information.
            	//TODO do something here
            	if (ToolPref.treatNotifCalendar(this)) {
        			Intent service = new Intent(this, MyService.class);
        			service.putExtra(MyService.KEY_NAME, MyService.KEY_NOTIF_CALENDAR);
        			// TODO to test
        			List<CharSequence> notificationList = event.getText();
        			service.putExtra(MyService.KEY_MESSAGE, notificationList.get(0));
        			this.startService(service); 
        			Log.i(getClass().getCanonicalName(), "Start Service Calendar");
        		}
            }
	    }
	}

	@Override
	protected void onServiceConnected() {
	    if (isInit) {
	        return;
	    }
	    AccessibilityServiceInfo info = new AccessibilityServiceInfo();
	    info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
	    info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;
	    setServiceInfo(info);
	    isInit = true;
	}

	@Override
	public void onInterrupt() {
	    isInit = false;
	}

}
