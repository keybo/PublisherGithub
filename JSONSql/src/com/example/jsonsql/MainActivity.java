package com.example.jsonsql;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

@SuppressLint("NewApi")
public class MainActivity extends Activity
{

	Database.OpenHelper a;
	//Database.Tables b;
	SQLiteDatabase db;
	Provider c;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ListView listViewthree =(ListView) findViewById(R.id.list);
		a=new Database.OpenHelper(this);
		//a.getWritableDatabase();
		//a.getReadableDatabase();
		c=new Provider();
		//startService(new Intent(MainActivity.this, ServiceProvider.class));
		Cursor cursor=c.query(Provider.CONTENT_URI, null, Database.C_ARTICLEID, null,null);
		String[] from =new String[]{Database.C_ARTCATEGORY,Database.C_ARTICLETITLE,Database.C_ARTICLEDESCRIPTION};
		int[] to=new int[] {  R.id.date,R.id.title,R.id.desc};
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,R.layout.item,cursor,from,to,0);
		listViewthree.setAdapter(adapter);
		
	
		
		Log.e("Right Message", "Right Massage");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
