package com.example.pub;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pub.db.Database;
import com.example.pub.utils.ImageLoader;

public class DetailsActivity extends Activity
{

	ImageLoader loader = null;
	int favorite=0;
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details);
		loader = new ImageLoader(this, R.drawable.ic_launcher);
		Intent intent = getIntent();
		if (intent != null)
		{
			Uri uri = intent.getData();
			if (uri != null)
			{
				@SuppressWarnings("deprecation")
				Cursor cursor = managedQuery(uri, new String[] 
						{
						BaseColumns._ID, Database.Project.C_PROJECTTITLE,
						Database.Project.C_PROJECTDESCRIPTION,
						Database.Project.C_SMALLIMAGE						
						 }, null, null, null);

				if (cursor == null) 
				{
					finish();
				} 
				else 
				{
					if (cursor.moveToFirst())
					{
						((TextView) findViewById(R.id.tTitle)).setText(cursor
								.getString(1));
						((TextView) findViewById(R.id.tDescription))
								.setText(Html.fromHtml(cursor.getString(2)));
						ImageView img = (ImageView) findViewById(R.id.smallImage);
						String imageUrl = cursor.getString(3);
						img.setTag(imageUrl);
						loader.DisplayImage(imageUrl, this, img);
					} else {
						finish();
					}

				}
			}
		}
	}

	 @Override
	    public boolean onCreateOptionsMenu(Menu menu)
	 {
	        getMenuInflater().inflate(R.menu.entry, menu);

	        /*MenuItem item = menu.findItem(R.id.menu_star);
	        item.setTitle(R.string.menu_unstar).setIcon(R.drawable.rating_important);*/
	        

	        return true;
	    }
	
	
}
