package com.example.pubui;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class UiActivity extends PreferenceActivity {
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		UiForPub.setPreferenceTheme(this);
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.activity_preferences);
	}

}
