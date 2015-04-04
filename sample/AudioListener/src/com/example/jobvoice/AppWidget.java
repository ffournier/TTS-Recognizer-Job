package com.example.jobvoice;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.jobvoice.ToolPref.ValueList;

public class AppWidget extends AppWidgetProvider {
	
	private static final String ALL_CLICK = "AllClick";
	private static final String SMS_CLICK = "SmsCLick";
	private static final String CALLBACK_CLICK = "CallBackClick";
	private static final String CALENDAR_CLICK = "CalendarClick";
	
	public static final String WIDGET_ID_KEY ="appwidgetid";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this
        // provider
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
		
	}
	
	private static final String ColorSelected = "#80B0FB";
	
	/**
     * Update the widget
     *
     * @param context
     * @param appWidgetManager
     * @param appWidgetId
     */
    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        // Prepare widget views
    	ValueList list = ToolPref.getType(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        
        views.setInt(R.id.all_widget, "setBackgroundColor", list == ValueList.ALL ? Color.GRAY : Color.TRANSPARENT);
        views.setInt(R.id.sms_widget, "setBackgroundColor", list == ValueList.SMS ? Color.GRAY : Color.TRANSPARENT);
        views.setInt(R.id.callback_widget, "setBackgroundColor", list == ValueList.CALLBACK ? Color.GRAY : Color.TRANSPARENT);
        views.setInt(R.id.calendar_widget, "setBackgroundColor", list == ValueList.NOTIF_CALENDAR ? Color.GRAY : Color.TRANSPARENT);
        
        views.setInt(R.id.all_widget_view, "setBackgroundColor", list == ValueList.ALL ? Color.parseColor(ColorSelected) : Color.TRANSPARENT);
        views.setInt(R.id.sms_widget_view, "setBackgroundColor", list == ValueList.SMS ? Color.parseColor(ColorSelected) : Color.TRANSPARENT);
        views.setInt(R.id.callback_widget_view, "setBackgroundColor", list == ValueList.CALLBACK ? Color.parseColor(ColorSelected) : Color.TRANSPARENT);
        views.setInt(R.id.calendar_widget_view, "setBackgroundColor", list == ValueList.NOTIF_CALENDAR ? Color.parseColor(ColorSelected) : Color.TRANSPARENT);
        
        
        views.setOnClickPendingIntent(R.id.all_widget, getPendingSelfIntent(context, ALL_CLICK, appWidgetId));
        views.setOnClickPendingIntent(R.id.sms_widget, getPendingSelfIntent(context, SMS_CLICK, appWidgetId));
        views.setOnClickPendingIntent(R.id.callback_widget, getPendingSelfIntent(context, CALLBACK_CLICK, appWidgetId));
        views.setOnClickPendingIntent(R.id.calendar_widget, getPendingSelfIntent(context, CALENDAR_CLICK, appWidgetId));
        
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		if (intent.hasExtra(WIDGET_ID_KEY)) {
			ValueList list = ToolPref.getType(context);
			if (ALL_CLICK.equals(intent.getAction())) {
		        if (list == ValueList.ALL) {
		        	ToolPref.setType(context, ValueList.NONE);
		        } else {
		        	ToolPref.setType(context, ValueList.ALL);
		        }
		        Log.w("Widget", "ALL_CLICK");
		    } else if (SMS_CLICK.equals(intent.getAction())) {
		    	if (list == ValueList.SMS) {
		        	ToolPref.setType(context, ValueList.NONE);
		        } else {
		        	ToolPref.setType(context, ValueList.SMS);
		        }
		        Log.w("Widget", "SMS_CLICK");
		    } else if (CALLBACK_CLICK.equals(intent.getAction())) {
		    	if (list == ValueList.CALLBACK) {
		        	ToolPref.setType(context, ValueList.NONE);
		        } else {
		        	ToolPref.setType(context, ValueList.CALLBACK);
		        }
		        Log.w("Widget", "CALLBACK_CLICK");
		    } else if (CALENDAR_CLICK.equals(intent.getAction())) {
		    	if (list == ValueList.NOTIF_CALENDAR) {
		        	ToolPref.setType(context, ValueList.NONE);
		        } else {
		        	ToolPref.setType(context, ValueList.NOTIF_CALENDAR);
		        }
		        Log.w("Widget", "CALENDAR_CLICK");
		    }
			int id = intent.getExtras().getInt(WIDGET_ID_KEY);
			this.updateAppWidget(context, AppWidgetManager.getInstance(context), id);
		}
	}

	protected PendingIntent getPendingSelfIntent(Context context, String action, int id) {
	    Intent intent = new Intent(context, getClass());
	    intent.setAction(action);
	    intent.putExtra(WIDGET_ID_KEY, id);
	    return PendingIntent.getBroadcast(context, 0, intent, 0);
	}
    
	
}
