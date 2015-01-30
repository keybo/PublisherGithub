package com.meta.pubui;


import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

public class PrefUtils 
{
	public static final String FIRST_OPEN = "FIRST_OPEN";
	public static final String IS_REFRESHING = "IS_REFRESHING";
	public static final String LIGHT_THEME = "lighttheme";
	
	public static final String LAST_SCHEDULED_REFRESH = "lastscheduledrefresh";
	public static final String REFRESH_ENABLED = "refresh.enabled";
	public static final String REFRESH_INTERVAL = "refresh.interval";
	public static final String REFRESH_WIFI_ONLY = "refreshwifionly.enabled";
	public static final String NOTIFICATIONS_ENABLED = "notifications.enabled";
	

	public static boolean getBoolean(String key, boolean defValue) 
	{
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(PubApplication.getContext());
		return settings.getBoolean(key, defValue);
	}

	public static void putBoolean(String key, boolean value)
	{
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(PubApplication.getContext())
				.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	//For Receiver
	 public static long getLong(String key, long defValue) 
	    {
	        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(PubApplication.getContext());
	        return settings.getLong(key, defValue);
	    }

	    public static void putLong(String key, long value)
	    {
	        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(PubApplication.getContext()).edit();
	        editor.putLong(key, value);
	        editor.commit();
	    }

	  //To register and unregister service
	    
	    public static void registerOnPrefChangeListener(OnSharedPreferenceChangeListener listener)
	    {
	        try
	        {
	            PreferenceManager.getDefaultSharedPreferences(PubApplication.getContext()).registerOnSharedPreferenceChangeListener(listener);
	        }
	        catch (Exception ignored) 
	        {
	        	// Seems to be possible to have a NPE here... Why??
	        }
	    }

	    public static void unregisterOnPrefChangeListener(OnSharedPreferenceChangeListener listener)
	    {
	        try 
	        {
	            PreferenceManager.getDefaultSharedPreferences(PubApplication.getContext()).unregisterOnSharedPreferenceChangeListener(listener);
	        }
	        catch (Exception ignored)
	        { 
	        	// Seems to be possible to have a NPE here... Why??
	        }
	    }
	    
	    //To the string
	    
	    public static String getString(String key, String defValue)
	    {
	        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(PubApplication.getContext());
	        return settings.getString(key, defValue);
	    }

	    public static void putString(String key, String value) 
	    {
	        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(PubApplication.getContext()).edit();
	        editor.putString(key, value);
	        editor.commit();
	    }
}
