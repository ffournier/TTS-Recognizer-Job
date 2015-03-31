package com.example.jobvoice;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.preference.PreferenceManager;

public class ToolPref {

	public static boolean treatSMS(Context context) {
		String value = PreferenceManager.getDefaultSharedPreferences(context).getString("type_preference_job", ValueList.ALL.getValueString());
		ValueList valList = ValueList.fromString(value);
		return valList == ValueList.ALL || valList == ValueList.SMS;
	}
	
	public static boolean treatCallBack(Context context) {
		String value = PreferenceManager.getDefaultSharedPreferences(context).getString("type_preference_job", ValueList.ALL.getValueString());
		ValueList valList = ValueList.fromString(value);
		return valList == ValueList.ALL || valList == ValueList.CALLBACK;
	}
	
	public static boolean treatNotifCalendar(Context context) {
		String value = PreferenceManager.getDefaultSharedPreferences(context).getString("type_preference_job", ValueList.ALL.getValueString());
		ValueList valList = ValueList.fromString(value);
		return valList == ValueList.ALL || valList == ValueList.NOTIF_CALENDAR;
	}
	
	public static int getRetry(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getInt("retry_preference_job", 2);
	}
	
	/**
	 * Enum Class for Type (All, SMS, CALLBACK, NOTIF_CALENDAR, None)
	 * @author florian
	 *
	 */
	public enum ValueList {
		ALL(1),
		SMS(2),
		CALLBACK(3),
		NOTIF_CALENDAR(4),
		NONE(5);
		
		private int value;

		/**
		 * Constructor
		 * @param value
		 */
        private ValueList(int value) {
                this.value = value;
        }
        
        /**
         * Getter Value
         * @return
         */
        public int getValue() { return value;}
        
        /**
         * Getter String Value
         * @return
         */
        public String getValueString() { return String.valueOf(value);}
        
        /**
         * Map to store all value of Enum
         */
        private static final Map<Integer, ValueList> intToTypeMap = new HashMap<Integer, ValueList>();
        static {
            for (ValueList type : ValueList.values()) {
                intToTypeMap.put(type.value, type);
            }
        }

        /**
         * Static getter Value
         * @param i, value enum
         * @return
         */
        public static ValueList fromInt(int i) {
        	ValueList type = intToTypeMap.get(Integer.valueOf(i));
            if (type == null) 
                return ValueList.NONE;
            return type;
        }
        
        /**
         * Static getter Value
         * @param value: string value enum
         * @return
         */
        public static ValueList fromString(String value) {
        	ValueList type = intToTypeMap.get(Integer.parseInt(value));
            if (type == null) 
                return ValueList.NONE;
            return type;
        }
	}
}
