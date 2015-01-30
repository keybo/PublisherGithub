
package com.meta.pubreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import com.meta.jsonsql.Database;
import com.meta.jsonsql.ServiceProvider;
import com.meta.pubui.PrefUtils;
import com.meta.pubui.PubApplication;

public class BootCompletedBroadcastReceiver extends BroadcastReceiver 
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        PrefUtils.putLong(PrefUtils.LAST_SCHEDULED_REFRESH, 0);
        if (PrefUtils.getBoolean(PrefUtils.REFRESH_ENABLED, true))
        {
        	Database.OpenHelper dbhelper = new Database.OpenHelper(PubApplication.getContext());
            SQLiteDatabase db = dbhelper.getWritableDatabase();
           	PubApplication.getContext().startService(new Intent(PubApplication.getContext(), ServiceProvider.class));
        }
    }

}
