package com.meta.pubui;

import android.app.Activity;

public class UiForPub
{
	static public void setPreferenceTheme(Activity a) 
	{
		if (!PrefUtils.getBoolean(PrefUtils.LIGHT_THEME, true)) 
		{
			a.setTheme(android.R.style.Theme_Holo);
		}

	}

	/*
	 * static public int dpToPixel(int dp) { return (int) TypedValue
	 * .applyDimension(TypedValue.COMPLEX_UNIT_DIP,
	 * dp,PubApplication.getContext().getResources().getDisplayMetrics()); }
	 */
}