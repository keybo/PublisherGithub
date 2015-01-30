package com.example.publisherinterview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.pubfeed.JSONFeed;
import com.example.pubfeed.JSONFunctions;
import com.example.pubfeed.JSONItem;
import com.example.publisherarticle.R;

public class Splash_Int_Activity extends Activity 
{
	private static final String IntURL = "http://baburajannamalai.webs.com/Publisher/qaa.json";
	
	private JSONFeed feed=new JSONFeed();

	String fileName;
	JSONArray jsonarray;
	JSONObject js;
	List<HashMap<String,String>> arraylist;
	HashMap<String, String> map;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.int_splash);

		ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conMgr.getActiveNetworkInfo() == null)
		{
		
			if(!(feed==null))
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
									finish();
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
			AsyncLoadJSON asljf=new AsyncLoadJSON();
			asljf.execute();

		}

	}

	private void startListActivity(JSONFeed feed)
	{
		Bundle bundle = new Bundle();
		bundle.putSerializable("feed", feed);
		Intent intent = new Intent(Splash_Int_Activity.this, List_Int_Activity.class);
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
				//String b=(arraylist.get(i)).get("intid");
				String c=(arraylist.get(i)).get("inttitle");
				String d=(arraylist.get(i)).get("intimage");
				String e=(arraylist.get(i)).get("intdate");
				String f=(arraylist.get(i)).get("intdescription");
				String auhthorName=(arraylist.get(i)).get("author");
				JSONItem item=new JSONItem();
				item.setDescription(f);
				item.setTitle(c);
				item.setImage(d);
				item.setDate(e);
				item.setAuthorName(auhthorName);
				
				feed.addItem(item);
				}
			} 
			catch (JSONException e)
			{
				Log.e("Error", e.getMessage());
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

	
	



