package com.example.publisherarticle;

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

import com.example.pubfeed.JSONFeed;
import com.example.pubfeed.JSONFunctions;
import com.example.pubfeed.JSONItem;

public class SplashActivity extends Activity 
{
	
	private String ArtURL ="http://10.0.2.2/Article/fetcharticle.php";
	
	private JSONFeed feed=new JSONFeed();

	
	Context context;

    
    
	String fileName;
	JSONArray jsonarray;
	List<HashMap<String,String>> arraylist;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.splash);

		
		 context=getApplicationContext();
		 
		 
		 
		ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conMgr.getActiveNetworkInfo() == null)
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
			AsyncLoadJsonFeed aslxf=new AsyncLoadJsonFeed();
			aslxf.execute();

		}

	}
	
	
	

	@Override
	protected void onResume() 
	{
		super.onResume();
		
	}
	
	
	

	private void startLisActivity(JSONFeed feed)
	{

		Bundle bundle = new Bundle();
		bundle.putSerializable("feed", feed);
		Intent intent = new Intent(SplashActivity.this, ListActivityOne.class);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();

	}

	private class AsyncLoadJsonFeed extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected Void doInBackground(Void... params)
		{
			arraylist = new ArrayList<HashMap<String, String>>();
			//JSONFunctions a=new JSONFunctions();
		
			 jsonarray= JSONFunctions.getJSONfromURL(ArtURL);
		try
			{
			for (int i = 0; i < jsonarray.length(); i++)
				{
					HashMap<String, String> map = new HashMap<String, String>();
					JSONObject js=jsonarray.getJSONObject(i);
					map.put("artid",js.getString("articleid"));
					map.put("arttitle",js.getString("articletitle"));
					map.put("artdescription",js.getString("articledescription"));
					map.put("artimage", js.getString("articleimage"));
					map.put("artdate",js.getString("updatedtime"));
					arraylist.add(map);
				}
			for(int i=0;i<arraylist.size();i++)
				{
				//String b=(arraylist.get(i)).get("artid");
				String c=(arraylist.get(i)).get("arttitle");
				String d=(arraylist.get(i)).get("artimage");
				String e=(arraylist.get(i)).get("artdate");
				Log.d(""+e,""+e);
			/*	*/
				String f=(arraylist.get(i)).get("artdescription");
				JSONItem item=new JSONItem();
				item.setDescription(f);
				item.setTitle(c);
				item.setImage(d);
				item.setDate(e);
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
			startLisActivity(feed);
			
		}

	}
	
	
	}

	
	



