package com.example.pubui;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefUtils {
	public static final String IS_REFRESHING = "IS_REFRESHING";
	public static final String LIGHT_THEME = "lighttheme";

	public static boolean getBoolean(String key, boolean defValue) {
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(PubApplication.getContext());
		return settings.getBoolean(key, defValue);
	}

	public static void putBoolean(String key, boolean value) {
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(PubApplication.getContext())
				.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

}
