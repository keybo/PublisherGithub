package com.meta.pubui;

import android.content.Context;
import android.content.Intent;

public final class CommonUtilities 
{
	static final String SERVER_URL = "http://192.168.56.1:8080/gcm-demo-server";

	public static final String SENDER_ID = "666222091983";

	static final String TAG = "Error";

	public static final String DISPLAY_MESSAGE_ACTION = "com.meta.pubui.DISPLAY_MESSAGE";

	public static final String EXTRA_MESSAGE = "message";

	static void displayMessage(Context context, String message) 
	{
		Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
		intent.putExtra(EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}
}
