package com.example.pub;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.pub.db.Database;
import com.example.pub.provider.LooserProvider;
import com.example.pub.services.LooserSync;
import com.example.pub.utils.ImageLoader;

public class MainActivity extends Activity implements OnItemClickListener {
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		doSync();
		ListView listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(this);
		listView.setAdapter(new MySimpleCursorAdapter(this, R.layout.itemrow,
				managedQuery(Uri.withAppendedPath(LooserProvider.CONTENT_URI,
						Database.Project.NAME), new String[] { BaseColumns._ID,
						Database.Project.C_PROJECTTITLE,
						Database.Project.C_ORGANIZATIONTITLE,
						Database.Project.C_SMALLIMAGE }, null, null, null),//"RANDOM() LIMIT 3"),
				new String[] { Database.Project.C_PROJECTTITLE,
						Database.Project.C_ORGANIZATIONTITLE,
						Database.Project.C_SMALLIMAGE }, new int[] {
						R.id.tName, R.id.tDescription, R.id.iItem }));
	}

	void doSync()
	{
		Intent serviceIntent = new Intent(this, LooserSync.class);
		startService(serviceIntent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.ui_menu_sync:
			doSync();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> l, View v, int position, long id)
	{
		Intent listIntent = new Intent(this, DetailsActivity.class);
		listIntent.setData(Uri.withAppendedPath(Uri.withAppendedPath(
				LooserProvider.CONTENT_URI, Database.Project.NAME), Long
				.toString(id)));
		startActivity(listIntent);

	}

	class MySimpleCursorAdapter extends SimpleCursorAdapter 
	{

		public MySimpleCursorAdapter(Context context, int layout, Cursor c,
				String[] from, int[] to) {
			super(context, layout, c, from, to);
			loader = new ImageLoader(context, R.drawable.ic_launcher);
		}

		ImageLoader loader = null;

		@Override
		public void setViewImage(ImageView v, String value) {
			v.setTag(value);
			loader.DisplayImage(value, (Activity)v.getContext(), v);
		}

		// @Override
		// public void setViewText(TextView v, String value) {
		// v.setText(Html.fromHtml(value));
		// }
	}
}