package com.example.pubui;

import android.app.Application;
import android.content.Context;

public class PubApplication extends Application 
{
	private static Context context;

	@Override
	public void onCreate()
	{
		super.onCreate();
		context = getApplicationContext();
		PrefUtils.putBoolean(PrefUtils.IS_REFRESHING, false); // init
	}

	public static Context getContext() 
	{
		return context;
	}
}
