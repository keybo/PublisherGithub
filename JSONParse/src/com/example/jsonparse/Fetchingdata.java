package com.example.jsonparse;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;


public class Fetchingdata extends Activity 
{
	// Declare Variables
	
	JSONArray jsonarray;
	ListView listview;
	ListViewAdapter adapter;
	ProgressDialog mProgressDialog;
	ArrayList<HashMap<String, String>> arraylist;
	static String RANK = "artid";
	static String COUNTRY = "arttitle";
	static String POPULATION ="artdescription";
	static String FLAG = "artimage";
	public static final String url="http://10.0.2.2/article/fetchdata.php";
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// Get the view from listview_main.xml
		setContentView(R.layout.listview_main);
		// Execute DownloadJSON AsyncTask
		new DownloadJSON().execute();
	}

	// DownloadJSON AsyncTask
	private class DownloadJSON extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			// Create a progressdialog
			mProgressDialog = new ProgressDialog(Fetchingdata.this);
			// Set progressdialog title
			mProgressDialog.setTitle("Publisher");
			// Set progressdialog message
			mProgressDialog.setMessage("Loading...");
			mProgressDialog.setIndeterminate(false);
			// Show progressdialog
			mProgressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params)
		{
			// Create the array 
			arraylist = new ArrayList<HashMap<String, String>>();
			JSONfunctions a=new JSONfunctions();
			 jsonarray= a.getJSONfromURL(url);

			try {
				

				for (int i = 0; i < jsonarray.length(); i++)
				{
					HashMap<String, String> map = new HashMap<String, String>();
					JSONObject js=jsonarray.getJSONObject(i);
						
					map.put("artid",js.getString("artid"));
					map.put("arttitle",js.getString("arttitle"));
					map.put("artdescription",js.getString("artdescrption"));
					map.put("artimage", js.getString("artimage"));
					map.put("artdate",js.getString("artdate"));
					// Set the JSON Objects into the array
					arraylist.add(map);
				}
			} catch (JSONException e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void args)
		{
			// Locate the listview in listview_main.xml
			listview = (ListView) findViewById(R.id.listview);
			// Pass the results into ListViewAdapter.java
			adapter = new ListViewAdapter(Fetchingdata.this, arraylist);
			// Binds the Adapter to the ListView
			listview.setAdapter(adapter);
			// Close the progressdialog
			mProgressDialog.dismiss();
		}
	}
}