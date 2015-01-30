package com.example.pubfavorite;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.pubsqlite.DatabaseHelper;
import com.example.pubui.R;
import com.example.pubui.UiForPub;
@SuppressLint("NewApi")
public class FavoriteList extends Activity 
{
	
	DatabaseHelper db;	
	Cursor cursor;
	FavListAdapater fla;

	// CustomCursorAdapter cca;
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		UiForPub.setPreferenceTheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fav_list);
	
		ListView listViewthree =(ListView) findViewById(R.id.list);
		db = new DatabaseHelper(this);
		db.openToRead();
		
	    cursor= db.getAllData();
		startManagingCursor(cursor);
	    Log.d("Data from note"+cursor,"Data from note"+cursor);
	    String[] from =new String[]{DatabaseHelper.KEY_ID,DatabaseHelper.FAV_TITLE,DatabaseHelper.FAV_DESC,DatabaseHelper.FAV_IMAGE};
	    int[] to=new int[] {  R.id.date,R.id.title,R.id.desc,R.id.thumb};
	    //Cursor c = sql.query(DatabaseHelper.TABLE_FAVORITE, null, null, null, null, null, null);
	    SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,R.layout.list_item,cursor,from,to);
	    adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder()
	    {
			
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex)
			{
				
				if(view.getId()==R.id.thumb)
				{
					//ImageView iv=(ImageView)view;
					//int getId=getApplicationContext().getResources().getIdentifier(cursor.getString(columnIndex), "drawable", getApplicationContext().getPackageName());
					//iv.setImageDrawable(getApplicationContext().getResources().getDrawable(getId));
					((ImageView)view).setImageDrawable(Imagehandler(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FAV_IMAGE))));
					
					return true;
				}
				
				return false;
			}
		});
	    
	  
	   // adapter = new SimpleCursorAdapter(this,R.layout.fav_list_item,d,new String[] { c.getString(c.getColumnIndex(DatabaseHelper.FAV_IMAGE)),c.getString(c.getColumnIndex(DatabaseHelper.FAV_TIME))},new int[] { R.id.tv_person_name, R.id.tv_person_pin,},0);
	    fla=new FavListAdapater(this,R.layout.list_item,cursor,from,to);
		listViewthree.setAdapter(adapter);
		listViewthree.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3)
			{
				
				cursor=(Cursor)arg0.getItemAtPosition(arg2);
				String keyid=cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_ID));
				String keyTitle=cursor.getString(cursor.getColumnIndex(DatabaseHelper.FAV_TITLE));
				/*String keyDesc=cursor.getString(cursor.getColumnIndex(DatabaseHelper.FAV_DESC));
				String keyImage=cursor.getString(cursor.getColumnIndex(DatabaseHelper.FAV_IMAGE));
				String keyTime=cursor.getString(cursor.getColumnIndex(DatabaseHelper.FAV_TIME));*/
				Bundle a=new Bundle();
				Log.e("A tag"+keyid, "A tag"+keyid);
				//a.putString("id",keyid);
				/*a.putString("title",keyTitle);
				a.putString("desc", keyDesc);
				a.putString("image", keyImage);
				a.putString("time", keyTime);*/
				Intent start=new Intent(FavoriteList.this,FavoriteAct.class);
				start.putExtras(a);
				start.putExtra("keyid",keyTitle);
				startActivity(start);
				
				
				
			}
		});

		db.close();
	   }
		
	protected Drawable Imagehandler(String url)
    {
        try 
        {
            url=url.replaceAll(" ", "%20");
            InputStream is = (InputStream)this.fetch(url);
            Drawable d = Drawable.createFromStream(is, "src");
            return d;
        } 
        catch (MalformedURLException e)
        {   
            System.out.println(url);
            System.out.println("error at URI"+e);
            return null;
        } 
        catch (IOException e) 
        {
            System.out.println("io exception: "+e);
            System.out.println("Image NOT FOUND");
            return null;
        } 
    }

    protected Object fetch(String address) throws MalformedURLException,IOException 
    {
        URL url = new URL(address);
        Object content = url.getContent();
        return content;
    }   
	}
	
	

