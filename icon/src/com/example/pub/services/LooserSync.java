package com.example.pub.services;


import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.example.pub.db.Database;
import com.example.pub.provider.LooserProvider;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class LooserSync extends IntentService
{

	public LooserSync() 
	{
		super("LooserSyncService");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Database.OpenHelper dbhelper = new Database.OpenHelper(getBaseContext());
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		DefaultHttpClient httpClient = new DefaultHttpClient();
		db.beginTransaction();
		HttpGet request = new HttpGet(
				"http://baburajannamalai.webs.com/Publisher/qaa.json");
		try
		{
			HttpResponse response = httpClient.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			{
				String bufstring = EntityUtils.toString(response.getEntity(), "UTF-8");
				JSONArray arr = new JSONArray(bufstring);
				Database.Tables tab = Database.Tables.AllTables
						.get(Database.Project.NAME);
				tab.DeleteAll(db);
				for (int i = 0; i < arr.length(); i++) {
					tab.InsertJSON(db, (JSONObject) arr.get(i));
				}
				db.setTransactionSuccessful();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		db.endTransaction();
		db.close();
		getContentResolver().notifyChange(
				Uri.withAppendedPath(LooserProvider.CONTENT_URI,
						Database.Projectone.NAME), null);

	}

}
