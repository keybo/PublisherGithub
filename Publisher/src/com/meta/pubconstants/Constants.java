package com.meta.pubconstants;




import java.text.DateFormat;

import android.app.NotificationManager;
import android.content.Context;

import com.meta.pubui.PubApplication;

public final class Constants 
{

	public static final String EXTENDED_FULLSCREEN =
            "com.meta.pubui.EXTENDED_FULLSCREEN";
    public static NotificationManager NOTIF_MGR = (NotificationManager) PubApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);

    public static final DateFormat DATE_FORMAT = android.text.format.DateFormat.getDateFormat(PubApplication.getContext());
    public static final DateFormat TIME_FORMAT = android.text.format.DateFormat.getTimeFormat(PubApplication.getContext());

    public static final String INTENT_FROM_WIDGET = "fromWidget";

    public static final String FEED_ID = "feedid";
   

   

 
   
   

  
  
 
    

    
    public static final int UPDATE_THROTTLE_DELAY = 1000;
}
