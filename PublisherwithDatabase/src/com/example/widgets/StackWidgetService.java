package com.example.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.pubfeed.JSONFunctions;
import com.example.pubui.R;

public class StackWidgetService extends RemoteViewsService {
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
	}
}

class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

	private List<WidgetItem> mWidgetItems = new ArrayList<WidgetItem>();
	private static final String IntURL = "http://baburajannamalai.webs.com/Publisher/qaa.json";
	String fileName;
	JSONArray jsonarray;
	JSONObject js;
	List<HashMap<String, String>> arraylist;
	HashMap<String, String> map;
	// WidgetItem listItem;
	private Context mContext = null;
	private int mAppWidgetId;

	public StackRemoteViewsFactory(Context context, Intent intent) {
		mContext = context;
		mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);

	}

	@Override
	public void onCreate() {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		populateListItem();

	}

	private void populateListItem() {
		arraylist = new ArrayList<HashMap<String, String>>();
		// JSONFunctions a=new JSONFunctions();
		jsonarray = JSONFunctions.getJSONfromURL(IntURL);
		try {
			for (int i = 0; i < jsonarray.length(); i++) {
				map = new HashMap<String, String>();
				js = jsonarray.getJSONObject(i);
				map.put("intid", js.getString("interviewid"));
				map.put("inttitle", js.getString("interviewtitle"));
				map.put("intdescription", js.getString("interviewdescription"));
				map.put("intimage", js.getString("interviewimage"));
				map.put("intdate", js.getString("updatedtime"));
				map.put("author", js.getString("authorname"));

				arraylist.add(map);
			}
			for (int i = 0; i < arraylist.size(); i++) {

				String c = (arraylist.get(i)).get("inttitle");
				// String d=(arraylist.get(i)).get("intdate");

				mWidgetItems.add(new WidgetItem(c));

			}
		} catch (Exception e) {

		}

	}

	public void onDestroy() {

		mWidgetItems.clear();
	}

	public int getCount() {
		return mWidgetItems.size();
	}

	public RemoteViews getViewAt(int position) {

		RemoteViews rv = new RemoteViews(mContext.getPackageName(),
				R.layout.widget_item);
		rv.setTextViewText(R.id.widget_item, mWidgetItems.get(position).text);

		Bundle extras = new Bundle();
		extras.putInt(StackWidgetProvider.EXTRA_ITEM, position);
		Intent fillInIntent = new Intent();
		fillInIntent.putExtras(extras);
		rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent);

		try {
			System.out.println("Loading view " + position);
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return rv;
	}

	public RemoteViews getLoadingView() {

		return null;
	}

	public int getViewTypeCount() {
		return 1;
	}

	public long getItemId(int position) {
		return position;
	}

	public boolean hasStableIds() {
		return true;
	}

	public void onDataSetChanged() {

	}

}