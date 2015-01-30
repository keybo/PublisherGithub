package com.example.publisher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.pubfeed.JSONFeed;
import com.example.pubfeed.JSONFunctions;
import com.example.pubfeed.JSONItem;
import com.example.publisherarticle.R;

public class PublisherSplash extends Activity 
{
	private String ArtURL ="http://www.baburajannamalai.webs.com/Publisher/video1.json";

	private JSONFeed feed=new JSONFeed();
     
	JSONArray jsonarray;
	List<HashMap<String,String>> arraylist;
	HashMap<String, String> map;
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		

		setContentView(R.layout.splash_v);
	
		ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conMgr.getActiveNetworkInfo() == null)
		{
		
			if(feed==null)
			{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(
						"Unable to reach server, \nPlease check your connectivity.")
						.setTitle("Publisher in MetaSoft")
						.setCancelable(false)
						.setPositiveButton("Exit",new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog,int id)
								{
								PublisherSplash.this.finish();
								}
								});

				AlertDialog alert = builder.create();
				alert.show();
			} 
			else
			{
			Toast.makeText(getApplicationContext(), "No connectivity! Please Check the Connection...", Toast.LENGTH_LONG).show();
			startListActivity(feed);
			}
			
		}
		
		else 
		{
			AsyncLoadJSON aslxf=new AsyncLoadJSON();
			aslxf.execute();
		}

	}

	@Override
	protected void onResume() 
	{
		super.onResume();
		
	}
	
	
	


	private void startListActivity(JSONFeed feed)
	{

		Bundle bundle = new Bundle();
		bundle.putSerializable("feed", feed);
		Intent intent = new Intent(PublisherSplash.this, PublisherList.class);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();

	}

	private class AsyncLoadJSON extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected Void doInBackground(Void... params)
		{
			arraylist = new ArrayList<HashMap<String, String>>();
			//JSONFunctions a=new JSONFunctions();
		
			 jsonarray= JSONFunctions.getJSONfromURL(ArtURL);
			 JSONObject js;
		try
			{
			for (int i = 0; i < jsonarray.length(); i++)
				{
					map = new HashMap<String, String>();
					js=jsonarray.getJSONObject(i);
				//	map.put("artid",js.getString("interviewid"));
					map.put("arttitle",js.getString("interviewtitle"));
					
					map.put("artdescription",js.getString("interviewdescription"));
					map.put("artimage", js.getString("interviewimage"));
					map.put("artdate",js.getString("updatedtime"));
					map.put("artvedio", js.getString("artvideo"));
					//map.put("author",js.getString("authorname"));
					arraylist.add(map);
					
					
				}
			for(int i=0;i<arraylist.size();i++)
				{
				//String b=(arraylist.get(i)).get("artid");
				String c=(arraylist.get(i)).get("arttitle");
				String d=(arraylist.get(i)).get("artimage");
				String e=(arraylist.get(i)).get("artdate");
				String f=(arraylist.get(i)).get("artdescription");
				String vedio=(arraylist.get(i).get("artvedio"));
				
				//String authorname=(arraylist.get(i)).get("author");
				JSONItem item=new JSONItem();
				item.setDescription(f);
				item.setTitle(c);
				item.setImage(d);
				item.setDate(e);
				item.setVideo(vedio);
				//item.setAuthorName(authorname);

				feed.addItem(item);
				}
			} 
			catch (JSONException e)
			{
				Log.e("Error", e.getMessage());
				Log.d("Error",""+feed);
				e.printStackTrace();
			}
			return null;

		}

		@Override
		protected void onPostExecute(Void result)
		{
			super.onPostExecute(result);
			Log.d("MESSAGE",""+feed);
			startListActivity(feed);
			
		}

	}
	
	
	}

	
	



