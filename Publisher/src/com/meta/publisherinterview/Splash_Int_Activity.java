package com.meta.publisherinterview;

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

import com.meta.pubfeed.JSONFeed;
import com.meta.pubfeed.JSONFunctions;
import com.meta.pubfeed.JSONItem;
import com.meta.pubsqlite.DatabaseHelper;
import com.meta.pubui.R;
import com.meta.pubui.UiForPub;

public class Splash_Int_Activity extends Activity {
	private static final String IntURL = "http://baburajannamalai.webs.com/Publisher/qaa.json";

	private JSONFeed feed = new JSONFeed();

	String fileName;
	JSONArray jsonarray;
	JSONObject js;
	DatabaseHelper db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		UiForPub.setPreferenceTheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.int_splash);

		db = new DatabaseHelper(this);

		db.openToWrite();

		ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conMgr.getActiveNetworkInfo() == null) {

			if (!(feed == null)) {
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
										finish();
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
		} else {
			AsyncLoadJSON asljf = new AsyncLoadJSON();
			asljf.execute();

		}

	}

	private void startListActivity(JSONFeed feed) {
		Bundle bundle = new Bundle();
		bundle.putSerializable("feed", feed);
		Intent intent = new Intent(Splash_Int_Activity.this,
				List_Int_Activity.class);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();

	}

	private class AsyncLoadJSON extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {

			// JSONFunctions a=new JSONFunctions();
			jsonarray = JSONFunctions.getJSONfromURL(IntURL);
			try {
				for (int i = 0; i < jsonarray.length(); i++) {
					js = jsonarray.getJSONObject(i);
					JSONItem a = new JSONItem();
					a.setDate(js.getString("updatedtime"));
					a.setImage(js.getString("interviewimage"));
					a.setTitle(js.getString("interviewtitle"));
					a.setAuthorName(js.getString("authorname"));
					a.setDescription(js.getString("interviewdescription"));

					db.createInterview(a);
					feed.addItem(a);

				}
			} catch (JSONException e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
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

}
