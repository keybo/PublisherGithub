package com.example.widget;

import android.appwidget.AppWidgetManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.util.Log;

import com.example.publisherarticle.R;

public class WidgetConfigActivity extends PreferenceActivity 
{
	private int widgetId;
	 @Override
	    protected void onCreate(Bundle bundle)
	    {
	        super.onCreate(bundle);
	        setResult(RESULT_CANCELED);

	        Bundle extras = getIntent().getExtras().getBundle("feed");
	        Log.d(""+extras, ""+extras);

	        if (extras != null) 
	        {
	            widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
	        }
	        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) 
	        {
	            finish();
	        }
	        addPreferencesFromResource(R.layout.widgetpreferences);
	        setContentView(R.layout.widgetconfig);
	        final PreferenceCategory feedsPreferenceCategory = (PreferenceCategory) findPreference("widget.visiblefeeds");
	    }
	 }
