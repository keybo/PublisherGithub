package com.example.publisherarticle;

import static com.example.publisherarticle.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.example.publisherarticle.CommonUtilities.EXTRA_MESSAGE;

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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.pubfeed.JSONFeed;
import com.example.pubfeed.JSONFunctions;
import com.example.pubfeed.JSONItem;
import com.google.android.gcm.GCMRegistrar;
public class SplashActivity extends Activity 
{
	
	private String ArtURL ="http://www.baburajannamalai.webs.com/Publisher/articlerecent.json";

	private JSONFeed feed=new JSONFeed();
  
	 //For GCM
	 Context context;
	 String regid;
   
	JSONArray jsonarray;
	List<HashMap<String,String>> arraylist;
	HashMap<String, String> map;
	AsyncTask<Void, Void, Void> mRegisterTask;

	   
	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		//For GCM
		
			context=getApplicationContext();
		    GCMRegistrar.checkDevice(this);
	        GCMRegistrar.checkManifest(this);
	        registerReceiver(mHandleMessageReceiver,new IntentFilter(DISPLAY_MESSAGE_ACTION));
	       
	       
		 
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
									SplashActivity.this.finish();
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
		Intent intent = new Intent(SplashActivity.this,ListActivityOne.class);
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
					map.put("artid",js.getString("articleid"));
					map.put("arttitle",js.getString("articletitle"));
					map.put("artdescription",js.getString("articledescription"));
					map.put("artimage", js.getString("articleimage"));
					map.put("artdate",js.getString("updatedtime"));
					map.put("author",js.getString("authorname"));
					arraylist.add(map);
				}
			for(int i=0;i<arraylist.size();i++)
				{
				//String b=(arraylist.get(i)).get("artid");
				String c=(arraylist.get(i)).get("arttitle");
				String d=(arraylist.get(i)).get("artimage");
				String e=(arraylist.get(i)).get("artdate");
				String f=(arraylist.get(i)).get("artdescription");
				String authorname=(arraylist.get(i)).get("author");
				Log.d("This is name "+authorname, ""+authorname);
				JSONItem item=new JSONItem();
				item.setDescription(f);
				item.setTitle(c);
				item.setImage(d);
				item.setDate(e);
				item.setAuthorName(authorname);

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
	private final BroadcastReceiver mHandleMessageReceiver =new BroadcastReceiver()
	{
	    @Override
	    public void onReceive(Context context, Intent intent) 
	    {
	        String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
	        Toast.makeText(getApplicationContext(), ""+newMessage, Toast.LENGTH_LONG).show();
	    }
	};
	
	}

	
	



