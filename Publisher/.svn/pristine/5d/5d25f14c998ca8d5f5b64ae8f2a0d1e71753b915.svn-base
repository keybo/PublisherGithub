package com.meta.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.meta.publisherarticle.SplashActivity;
import com.meta.pubui.R;

public class StackWidgetProvider extends AppWidgetProvider 
{
	public static final String TOAST_ACTION = "com.meta.widgets.TOAST_ACTION";
	public static final String EXTRA_ITEM = "com.meta.widgets.EXTRA_ITEM";
	public static String WIDGET_UPDATE = "com.meta.widgets.WIDGET_UPDATE";
	public static String newdata;

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
	}

	@Override
	public void onEnabled(Context context) 
	{
		System.out.println("new task flag");
	    Intent i = new Intent(context, SplashActivity.class);
	    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    context.startActivity(i);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle getPrevData = intent.getExtras();
		String data = getPrevData.getString("feed");
		newdata = data;
		Log.d("Data from Activity" + newdata, "Data from Activity" + newdata);
		if (intent.getAction().equals(TOAST_ACTION))
		{
			int appWidgetId = intent.getIntExtra(
					AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
			int viewIndex = intent.getIntExtra(EXTRA_ITEM, 0);

			Toast.makeText(context, "Touched view " + viewIndex,
					Toast.LENGTH_SHORT).show();
		}

		super.onReceive(context, intent);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		for (int i = 0; i < appWidgetIds.length; ++i) {

			Intent intent = new Intent(context, StackWidgetService.class);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
					appWidgetIds[i]);

			intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
			RemoteViews rv = new RemoteViews(context.getPackageName(),
					R.layout.widget_layout);
			rv.setOnClickPendingIntent(R.id.widget_item, PendingIntent
					.getActivity(context, 0, new Intent(context,
							SplashActivity.class), 0));
			rv.setRemoteAdapter(appWidgetIds[i], R.id.stack_view, intent);

			rv.setEmptyView(R.id.stack_view, R.id.empty_view);

			Intent toastIntent = new Intent(context, StackWidgetProvider.class);
			toastIntent.setAction(StackWidgetProvider.TOAST_ACTION);
			toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
					appWidgetIds[i]);

			PendingIntent toastPendingIntent = PendingIntent.getBroadcast(
					context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent);

			appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
}