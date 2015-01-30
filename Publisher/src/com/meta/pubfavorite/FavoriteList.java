package com.meta.pubfavorite;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.meta.pubsqlite.DatabaseHelper;
import com.meta.pubui.R;
import com.meta.pubui.UiForPub;

@SuppressLint("NewApi")
public class FavoriteList extends Activity {

	DatabaseHelper db;
	Cursor cursor;
	FavListAdapater fla;
	SimpleCursorAdapter adapter;
	ListView favList;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		UiForPub.setPreferenceTheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fav_list);
		favList = (ListView) findViewById(R.id.list);
		db = new DatabaseHelper(this);
		db.openToRead();

		cursor = db.getAllData();
		startManagingCursor(cursor);
		Log.d("Data from note" + cursor, "Data from note" + cursor);
		String[] from = new String[] { DatabaseHelper.KEY_ID,
				DatabaseHelper.FAV_TITLE, DatabaseHelper.FAV_DESC,
				DatabaseHelper.FAV_IMAGE };
		int[] to = new int[] { R.id.date, R.id.title, R.id.desc, R.id.thumb };
		// Cursor c = sql.query(DatabaseHelper.TABLE_FAVORITE, null, null, null,
		// null, null, null);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			adapter = new SimpleCursorAdapter(this, R.layout.list_item, cursor,
					from, to, 0);
		} else {
			adapter = new SimpleCursorAdapter(this, R.layout.list_item, cursor,
					from, to);
		}
		favList.setAdapter(adapter);
		adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

			@Override
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {

				if (view.getId() == R.id.thumb) {

					((ImageView) view).setImageDrawable(Imagehandler(cursor
							.getString(cursor
									.getColumnIndex(DatabaseHelper.FAV_IMAGE))));

					return true;
				}

				return false;
			}
		});

		fla = new FavListAdapater(this, R.layout.list_item, cursor, from, to);

		favList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {

				
				// int pos = arg2;

				Intent b = new Intent(FavoriteList.this, FavoriteAct.class);
				b.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				//Cursor cursor = (Cursor) arg0.getItemAtPosition(position);
				b.putExtra("title",
						cursor.getColumnIndex(DatabaseHelper.FAV_TITLE));
				b.putExtra("desc",
						cursor.getColumnIndex(DatabaseHelper.FAV_DESC));
				b.putExtra("time",
						cursor.getColumnIndex(DatabaseHelper.FAV_TIME));
				b.putExtra("image",
						cursor.getColumnIndex(DatabaseHelper.FAV_IMAGE));

				//Toast.makeText(getApplicationContext(), "" + cursor,
					//	Toast.LENGTH_SHORT).show();
				startActivity(b);

			}
		});

	}

	protected Drawable Imagehandler(String url) {
		try {
			url = url.replaceAll(" ", "%20");
			InputStream is = (InputStream) this.fetch(url);
			Drawable d = Drawable.createFromStream(is, "src");
			return d;
		} catch (MalformedURLException e) {
			System.out.println(url);
			System.out.println("error at URI" + e);
			return null;
		} catch (IOException e) {
			System.out.println("io exception: " + e);
			System.out.println("Image NOT FOUND");
			return null;
		}
	}

	protected Object fetch(String address) throws MalformedURLException,
			IOException {
		URL url = new URL(address);
		Object content = url.getContent();
		return content;
	}
}
