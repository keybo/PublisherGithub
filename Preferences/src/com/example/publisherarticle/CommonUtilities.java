
package com.example.publisherarticle;

import android.content.Context;
import android.content.Intent;


public final class CommonUtilities 
{
	static final String SERVER_URL = "http://10.0.2.2/gcm-demo-server";

	static final String SENDER_ID = "666222091983";
	


	static final String TAG = "Error";

	static final String DISPLAY_MESSAGE_ACTION = "com.example.publisherarticle.DISPLAY_MESSAGE";

	
	static final String EXTRA_MESSAGE = "message";

	
	static void displayMessage(Context context, String message) 
	{
		Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
		intent.putExtra(EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}
}
