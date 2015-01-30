package com.example.pubfavorite;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.pubimage.ImageLoader;
import com.example.pubsqlite.DatabaseHelper;
import com.example.pubui.R;

public class FavListAdapater extends SimpleCursorAdapter {
	private LayoutInflater mLayoutInflater;
	private int layout;
	DatabaseHelper db;
	ImageLoader imageLoader;

	private class ViewHolder {
		TextView title, time, desc;
		ImageView imageView;

		ViewHolder(View v) {
			title = (TextView) v.findViewById(R.id.title);
			time = (TextView) v.findViewById(R.id.date);
			desc = (TextView) v.findViewById(R.id.desc);
			imageView = (ImageView) v.findViewById(R.id.thumb);
		}
	}

	@SuppressWarnings("deprecation")
	public FavListAdapater(Context ctx, int layout, Cursor c, String[] from,
			int[] to) {
		super(ctx, layout, c, from, to);
		this.layout = layout;
		mLayoutInflater = LayoutInflater.from(ctx);
		imageLoader = new ImageLoader(ctx);

	}

	@Override
	public View newView(Context ctx, Cursor cursor, ViewGroup parent) {
		View vView = mLayoutInflater.inflate(layout, parent, false);
		vView.setTag(new ViewHolder(vView));
		return vView;
	}

	@Override
	public void bindView(View v, Context ctx, Cursor c) {
		db = new DatabaseHelper(ctx);
		c = db.getAllData();
		db.openToRead();
		String titleData = c.getString(c
				.getColumnIndex(DatabaseHelper.FAV_TITLE));
		String imageData = c.getString(c
				.getColumnIndex(DatabaseHelper.FAV_IMAGE)); // // path & file
		String descData = c
				.getString(c.getColumnIndex(DatabaseHelper.FAV_DESC));
		String timeData = c
				.getString(c.getColumnIndex(DatabaseHelper.FAV_TIME));
		ViewHolder vh = (ViewHolder) v.getTag();
		vh.title.setText(titleData);
		vh.desc.setText(descData);
		vh.time.setText(timeData);
		vh.imageView.setImageDrawable((Imagehandler(imageData)));

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