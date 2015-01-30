package com.meta.pubui;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;

public class PubApplication extends Application 
{
	private static Context context;

	Cursor sharedCursor;
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
	
	public Cursor getSharedCursor() 
    {
        return this.sharedCursor;
    }

    public void setSharedCursor(Cursor c)
    {
        this.sharedCursor = c;
    }
}
