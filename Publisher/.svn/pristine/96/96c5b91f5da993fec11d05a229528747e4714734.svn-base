package com.meta.jsonsql;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;


public class ServiceProvider extends IntentService 
{

    public ServiceProvider()
    {
        super("Service");
    }
    @Override
    protected void onHandleIntent(Intent intent)
    {
        Database.OpenHelper dbhelper = new Database.OpenHelper(getBaseContext());
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        DefaultHttpClient httpClient = new DefaultHttpClient();
        db.beginTransaction();
        HttpGet request = new HttpGet(
                "http://baburajannamalai.webs.com/Publisher/articlerecent.json");
        try
        {
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            {
                InputStream instream = response.getEntity().getContent();
                BufferedReader r = new BufferedReader(new InputStreamReader(
                        instream, "UTF-8"), 8000);
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null)
                {
                    total.append(line);
                }
               
                String bufstring = total.toString();
                JSONArray arr = new JSONArray(bufstring);
                Database.Tables tab = Database.Tables.AllTables.get(Database.NAME);
                tab.DeleteAll(db);
                for (int i = 0; i < arr.length(); i++) 
                {
                	JSONObject a=(JSONObject) arr.get(i);
                	Log.d("This is josn boject"+a,"This is josn boject"+a);
                    tab.InsertJSON(db, (JSONObject) arr.get(i));
                }
                db.setTransactionSuccessful();
                instream.close();
            }
            
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
        getContentResolver().notifyChange(Uri.withAppendedPath(Provider.CONTENT_URI,
                Database.NAME), null);
        db.endTransaction();
        db.close();
     }

}