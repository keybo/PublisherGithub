package com.example.metaactivity;

import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.example.metanews.R;
import com.example.metaprovider.DatabaseHelper;
import com.example.metaprovider.FeedData.EntryColumns;
import com.meta.pubfeed.JSONFeed;
import com.meta.pubfeed.JSONFunctions;
import com.meta.pubfeed.JSONItem;


@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class SplashActivity extends Activity 
{

	private String ArtURL = "http://baburajannamalai.webs.com/Publisher/articlerecent.json";

	private JSONFeed feed = new JSONFeed();

	Context context;

	JSONArray jsonarray;
	List<HashMap<String, String>> arraylist;
	AsyncTask<Void, Void, Void> mRegisterTask;

	DatabaseHelper db;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		// For GCM
		
		

		

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conMgr.getActiveNetworkInfo() == null) {

			if (feed == null) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(
						"Unable to reach server, \nPlease check your connectivity.")
						.setTitle("Publisher in MetaSoft")
						.setCancelable(false)
						.setPositiveButton("Exit",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										SplashActivity.this.finish();
									}
								});

				AlertDialog alert = builder.create();
				alert.show();
			} else {
				Toast.makeText(getApplicationContext(),
						"No connectivity! Please Check the Connection...",
						Toast.LENGTH_LONG).show();
				startListActivity(feed);
			}

		}

		else {
			AsyncLoadJSON aslxf = new AsyncLoadJSON();
			aslxf.execute();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	private void startListActivity(JSONFeed feed) {

		Bundle bundle = new Bundle();
		bundle.putSerializable("feed", feed);
		Intent intent = new Intent(SplashActivity.this, MainActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();

	}

	private class AsyncLoadJSON extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// arraylist = new ArrayList<HashMap<String, String>>();
			// JSONFunctions a=new JSONFunctions();

			jsonarray = JSONFunctions.getJSONfromURL(ArtURL);
			JSONObject js;
			JSONItem a ;
			ContentValues values=new ContentValues();
			ContentResolver cr=getContentResolver();
			try {
				for (int i = 0; i < jsonarray.length(); i++) {
					js = jsonarray.getJSONObject(i);
					
					values.put(EntryColumns.AUTHOR, js.getString("authorname"));
					Log.e(""+js.getString("authorname"), ""+js.getString("authorname"));
									
					
					
					
					

					// arraylist.add(map);
				}
			} catch (JSONException e) {
				Log.e("Error", e.getMessage());
				Log.d("Error", "" + feed);
				Thread.setDefaultUncaughtExceptionHandler(null);
			}

			return null;

		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			Log.d("MESSAGE", "" + feed);
			startListActivity(feed);

		}

	}
	

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		
		
		
	}
	
}
