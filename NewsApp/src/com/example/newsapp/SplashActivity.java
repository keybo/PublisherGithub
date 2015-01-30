package com.example.newsapp;

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
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.meta.pubfeed.JSONFeed;
import com.meta.pubfeed.JSONFunctions;
import com.meta.pubfeed.JSONItem;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class SplashActivity extends Activity {

	private String ArtURL = "http://baburajannamalai.webs.com/Publisher/articlerecent.json";
	private String INTURL = "http://baburajannamalai.webs.com/Publisher/qaa.json";

	private JSONFeed feed = new JSONFeed();

	Context context;

	JSONArray jsonarray;

	List<HashMap<String, String>> arraylist;
	AsyncTask<Void, Void, Void> mRegisterTask;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

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

		/*
		 * Bundle bundle = new Bundle(); bundle.putSerializable("feed", feed);
		 * Intent intent = new Intent(SplashActivity.this,
		 * SplashActivity.class); intent.putExtras(bundle);
		 * startActivity(intent); finish();
		 */

	}

	private class AsyncLoadJSON extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// arraylist = new ArrayList<HashMap<String, String>>();
			// JSONFunctions a=new JSONFunctions();

			jsonarray = JSONFunctions.getJSONfromURL(ArtURL);
			JSONObject js;
			ContentResolver resolver = getContentResolver();
			JSONItem a;
			try {
				for (int i = 0; i < jsonarray.length(); i++) {
					ContentValues values, values1, values2;
					values = new ContentValues();
					values1 = new ContentValues();
					values2 = new ContentValues();
					js = jsonarray.getJSONObject(i);
					a = new JSONItem();

					a.setDate(js.getString("updatedtime"));
					a.setImage(js.getString("articleimage"));
					a.setTitle(js.getString("articletitle"));
					a.setAuthorName(js.getString("authorname"));
					a.setDescription(js.getString("articledescription"));
					// feed.addItem(a);
					Log.e("Error" + a.getAuthorName(), "" + a.getDate());
					values.put(DbData.FeedColumnsone.ART_TITLE, a.getDate());

					values.put(DbData.FeedColumnsone.ART_TIME,
							a.getDescription());
					values.put(DbData.FeedColumnsone.ART_AUTHOR,
							a.getAuthorName());
					values.put(DbData.FeedColumnsone.ART_DESC,
							a.getDescription());
					values.put(DbData.FeedColumnsone.ART_IMAGE, a.getImage());

					values1.put(DbData.FeedColumnstwo.INT_TITLE, a.getTitle());
					values1.put(DbData.FeedColumnstwo.INT_TIME, a.getDate());
					values1.put(DbData.FeedColumnstwo.INT_AUTHOR,
							a.getAuthorName());
					values1.put(DbData.FeedColumnstwo.INT_DESC,
							a.getDescription());
					values1.put(DbData.FeedColumnstwo.INT_IMAGE, a.getImage());
					values2.put(DbData.UrlColumns.LINK, a.getImage());
					resolver.insert(DbData.CONTENT_URI, values);

				}

				// arraylist.add(map);
			}

			catch (JSONException e) {
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
	protected void onDestroy() {
		super.onDestroy();

	}

}
