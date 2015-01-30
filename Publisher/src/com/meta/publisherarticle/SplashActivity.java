package com.meta.publisherarticle;

import static com.meta.pubui.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.meta.pubui.CommonUtilities.EXTRA_MESSAGE;
import static com.meta.pubui.CommonUtilities.SENDER_ID;

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
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.meta.pubfeed.JSONFeed;
import com.meta.pubfeed.JSONFunctions;
import com.meta.pubfeed.JSONItem;
import com.meta.pubsqlite.DatabaseHelper;
import com.meta.pubui.R;
import com.meta.pubui.ServerUtilities;
import com.meta.pubui.UiForPub;

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

		UiForPub.setPreferenceTheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		// For GCM
		
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		final String regId = GCMRegistrar.getRegistrationId(this);
		Log.d("registrationID", "::" + regId);
		if (regId.equals("")) {
			// Automatically registers application on startup.
			GCMRegistrar.register(this, SENDER_ID);
		} else {
			// Device is already registered on GCM, needs to check if it is
			// registered on our server as well.
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// Skips registration.
				Toast.makeText(getApplicationContext(),
						getString(R.string.already_registered),
						Toast.LENGTH_LONG).show();
			} else {

				final Context context = this;
				mRegisterTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						boolean registered = ServerUtilities.register(context,
								regId);

						if (!registered) {
							GCMRegistrar.unregister(context);
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mRegisterTask = null;
					}

				};
				mRegisterTask.execute(null, null, null);
			}
		}

		db = new DatabaseHelper(this);
		db.openToWrite();

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
		Intent intent = new Intent(SplashActivity.this, ListActivityOne.class);
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
			try {
				for (int i = 0; i < jsonarray.length(); i++) {
					js = jsonarray.getJSONObject(i);
					a= new JSONItem();
					
					a.setDate(js.getString("updatedtime"));
					a.setImage(js.getString("articleimage"));
					a.setTitle(js.getString("articletitle"));
					a.setAuthorName(js.getString("authorname"));
					a.setDescription(js.getString("articledescription"));
					db.openToWrite();
					db.createArticle(a);
					feed.addItem(a);

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
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			Toast.makeText(getApplicationContext(), newMessage,
					Toast.LENGTH_LONG).show();
		}
	};

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if (mRegisterTask != null) 
		{
			mRegisterTask.cancel(true);
		}
		unregisterReceiver(mHandleMessageReceiver);
		GCMRegistrar.onDestroy(this);
		
	}
	
}
