package com.example.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;


import com.example.pubfeed.JSONFunctions;
import com.example.publisherarticle.R;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ListProvider implements RemoteViewsFactory {
	private ArrayList<ListItem> listItemList = new ArrayList<ListItem>();
	
	private static final String IntURL = "http://baburajannamalai.webs.com/Publisher/qaa.json";
	
	

	String fileName;
	JSONArray jsonarray;
	JSONObject js;
	List<HashMap<String,String>> arraylist;
	HashMap<String, String> map;
	private Context context = null;
	private int appWidgetId;
	ListItem listItem;
	public ListProvider(Context context, Intent intent)
	{
		this.context = context;
		appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);

		populateListItem();
	}

	private void populateListItem()
	{
		arraylist = new ArrayList<HashMap<String, String>>();
		//JSONFunctions a=new JSONFunctions();
		 jsonarray= JSONFunctions.getJSONfromURL(IntURL);
	try
		{
		for (int i = 0; i < jsonarray.length(); i++)
			{
				map = new HashMap<String, String>();
				js=jsonarray.getJSONObject(i);
				map.put("intid",js.getString("interviewid"));
				map.put("inttitle",js.getString("interviewtitle"));
				map.put("intdescription",js.getString("interviewdescription"));
				map.put("intimage", js.getString("interviewimage"));
				map.put("intdate",js.getString("updatedtime"));
				map.put("author",js.getString("authorname"));
				
				arraylist.add(map);
			}
		for(int i=0;i<arraylist.size();i++)
			{
			
			String c=(arraylist.get(i)).get("inttitle");
			
			String e=(arraylist.get(i)).get("intdate");
			
			listItem = new ListItem();
			listItem.heading = c;
			listItem.content = e;
			Log.d("This is string"+c, "This is Empty "+e);
			
			listItemList.add(listItem);
			}
		}
	catch(Exception e)
	{
		
	}
		
		
			
		

	}

	@Override
	public int getCount() {
		return listItemList.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/*
	 *Similar to getView of Adapter where instead of View
	 *we return RemoteViews 
	 * 
	 */
	@Override
	public RemoteViews getViewAt(int position) {
		final RemoteViews remoteView = new RemoteViews(
				context.getPackageName(), R.layout.list_row);
		ListItem listItem = listItemList.get(position);
		remoteView.setTextViewText(R.id.heading, listItem.heading);
		remoteView.setTextViewText(R.id.content, listItem.content);

		return remoteView;
	}
	

	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public void onCreate()
	{
	}

	@Override
	public void onDataSetChanged()
	{
		
	}

	@Override
	public void onDestroy()
	{
	}

}
