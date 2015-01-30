package com.meta.pubsqlite;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.meta.pubfeed.Favorite;
import com.meta.pubfeed.JSONItem;

public class DatabaseHelper 
{

	private Context context;
	private SQLiteHelper sqLiteHelper;
	private SQLiteDatabase sqLiteDatabase;
	// Logcat 
	private static final String LOG = "DatabaseHelper";

	// Database Version
	static final int DATABASE_VERSION = 1;

	// Database Name
	 static final String DATABASE_NAME = "Publisher.db";

	

	// Common column names
	 public static final String KEY_ID = "_id";
	 static final String KEY_CREATED_AT = "created_at";

	
	//Article Table 
	private static final String TABLE_ARTICLE = "article";
	private static final String ART_DESC="article_desc";
	private static final String ART_TITLE="article_title";
	private static final String ART_AUTHOR="article_author";
	private static final String ART_TIME="article_date";
	private static final String ART_IMAGE="artilce_image";
	
	//For Interview
	private static final String TABLE_INTERVIEW = "interview";
	private static final String INT_DESC="article_desc";
	private static final String INT_TITLE="article_title";
	private static final String INT_AUTHOR="article_author";
	private static final String INT_TIME="article_date";
	private static final String INT_IMAGE="artilce_image";

	//For favorite table
     public static final String TABLE_FAVORITE="favorite";
	 public static final String FAV_ID="fav_id";
	 public static final String FAV_TITLE="fav_title";
	 public static final String FAV_IMAGE="fav_image";
	 public static final String FAV_DESC="fav_desc";
	 public static final String FAV_TIME="fav_time";
	 
	 //To contact 
	 
	 
	//To Create Article table;
	private static final String CREATE_TABLE_ARTICLE = "CREATE TABLE "
			+ TABLE_ARTICLE + "(" + KEY_ID + " INTEGER PRIMARY KEY," + ART_DESC
			+ " TEXT," + ART_TITLE + " TEXT," + ART_AUTHOR + " TEXT," + ART_IMAGE + " TEXT,"  + ART_TIME + " DATETIME," + KEY_CREATED_AT
			+ " DATETIME" + ")";
	//To Create Interview table
	private static final String CREATE_TABLE_INTERVIEW = "CREATE TABLE "
			+ TABLE_INTERVIEW + "(" + KEY_ID + " INTEGER PRIMARY KEY," + INT_DESC
			+ " TEXT," + INT_TITLE + " TEXT," + INT_AUTHOR + " TEXT," + INT_IMAGE + " TEXT,"  + INT_TIME + " DATETIME," + KEY_CREATED_AT
			+ " DATETIME" + ")";

	//To Create Favorite table;
	  private static final String CREATE_TABLE_FAV = "CREATE TABLE "
			+ TABLE_FAVORITE + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + FAV_ID + " INTEGER," + FAV_TITLE + " TEXT," 
			+ FAV_IMAGE + " TEXT," + FAV_DESC + " TEXT," + FAV_TIME + " DATETIME," + KEY_CREATED_AT
			+ " DATETIME" + ")";

	 
	public DatabaseHelper(Context con)
	{
		context=con;
	}

	public DatabaseHelper openToRead() throws android.database.SQLException 
	{
		sqLiteHelper = new SQLiteHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
		sqLiteDatabase = sqLiteHelper.getReadableDatabase();
		return this;
	}
	
	public DatabaseHelper openToWrite() throws android.database.SQLException 
	{
		sqLiteHelper = new SQLiteHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
		sqLiteDatabase = sqLiteHelper.getWritableDatabase();
		return this;
	}
	
	
		
	
	public class SQLiteHelper extends SQLiteOpenHelper
	{

		public SQLiteHelper(Context context, String name,
				CursorFactory factory, int version) 
		{
			super(context, name, factory, version);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) 
		{
			db.execSQL(CREATE_TABLE_FAV);
			db.execSQL(CREATE_TABLE_ARTICLE);
			db.execSQL(CREATE_TABLE_INTERVIEW);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			
			/*db.execSQL("DROP TABLE IF EXISTS " +TABLE_ARTICLE);
			db.execSQL("DROP TABLE IF EXISTS "+TABLE_INTERVIEW);
			db.execSQL("DROP TABLE IF EXISTS "+CREATE_TABLE_FAV);
			// create new tables
			onCreate(db);*/
		}
		
	}
	
	public int deleteAll()
	{
		return sqLiteDatabase.delete(DATABASE_NAME, null, null);
	}
	
	
	public long createArticle(JSONItem item)
	{
		
	ContentValues values = new ContentValues();
	values.put(ART_TITLE, item.getTitle());
	values.put(ART_DESC,item.getDescription());
	values.put(KEY_CREATED_AT, getDateTime());
	values.put(ART_IMAGE,item.getImage());
	values.put(ART_AUTHOR, item.getAuthorName());
	values.put(ART_TIME, item.getDate());
	
	//To  insert row
	long article_id = sqLiteDatabase.insert(TABLE_ARTICLE, null, values);

	return article_id;
	}
	
	public long createInterview(JSONItem item)
	{
	

	ContentValues values = new ContentValues();
	values.put(INT_TITLE, item.getTitle());
	values.put(INT_DESC,item.getDescription());
	values.put(KEY_CREATED_AT, getDateTime());
	values.put(INT_IMAGE,item.getImage());
	values.put(INT_AUTHOR, item.getAuthorName());
	values.put(INT_TIME, item.getDate());
	
	// To insert row
	long interview_id =sqLiteDatabase.insert(TABLE_INTERVIEW, null, values);

	return interview_id;
	}
	//To list all values in Articles
	public List<JSONItem> getAllArticleTags() 
	{
		List<JSONItem> articleItems = new ArrayList<JSONItem>();
		String selectQuery = "SELECT  * FROM " + TABLE_ARTICLE;
		
		Cursor c = sqLiteDatabase.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst())
		{
			do 
			{
				Log.e(LOG, selectQuery);
				JSONItem t = new JSONItem();
				
				t.setTitle(c.getString(c.getColumnIndex(ART_TITLE)));
				t.setDate(c.getString(c.getColumnIndex(ART_TIME)));
				t.setImage(c.getString(c.getColumnIndex(ART_IMAGE)));
				t.setDescription(c.getString(c.getColumnIndex(ART_DESC)));
				t.setAuthorName(c.getString(c.getColumnIndex(ART_AUTHOR)));

				// adding to tags list
				articleItems.add(t);
			} 
			while (c.moveToNext());
		}
		return  articleItems;
	}
	
	//To List all values in interview
	public List<JSONItem> getAllInterviewTags() 
	{
		List<JSONItem> interviewItems = new ArrayList<JSONItem>();
		String selectQuery = "SELECT  * FROM " + TABLE_INTERVIEW;
		
		Cursor c = sqLiteDatabase.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst())
		{
			do 
			{
				Log.e(LOG, selectQuery);
				JSONItem t = new JSONItem();
				
				t.setTitle(c.getString(c.getColumnIndex(INT_TITLE)));
				t.setDate(c.getString(c.getColumnIndex(INT_TIME)));
				t.setImage(c.getString(c.getColumnIndex(INT_IMAGE)));
				t.setDescription(c.getString(c.getColumnIndex(INT_DESC)));
				t.setAuthorName(c.getString(c.getColumnIndex(INT_AUTHOR)));

				// adding to tags list
				interviewItems.add(t);
			} 
			while (c.moveToNext());
		}
		return  interviewItems;
	}
	
	public long createFav(Favorite fav)
	{
		
		ContentValues cv=new ContentValues();
		cv.put(FAV_ID,fav.getFav());
		cv.put(FAV_TITLE,fav.getFavTitle());
		cv.put(FAV_IMAGE, fav.getFavImage());
		cv.put(FAV_DESC, fav.getFavDesc());
		cv.put(FAV_TIME, fav.getDate());
		cv.put(KEY_CREATED_AT, getDateTime());
		long favorite=sqLiteDatabase.insert(TABLE_FAVORITE, null, cv);
		
		return favorite;
	}
	
	//Fetch item from favorite
	
	public List<Favorite> getAllFavorite() 
	{
		
		List<Favorite> favoriteItems = new ArrayList<Favorite>();
	
		String selectQuery = "SELECT  * FROM " + TABLE_FAVORITE;
		
		Cursor c = sqLiteDatabase.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst())
		{
			do 
			{
				Log.e(LOG, selectQuery);
				Favorite fav = new Favorite();
				fav.setFav(c.getInt(1));
				fav.setFavDesc(c.getString(c.getColumnIndex(FAV_DESC)));
				fav.setDate(c.getString(c.getColumnIndex(FAV_TIME)));
				fav.setImage(c.getString(c.getColumnIndex(FAV_IMAGE)));
				fav.setFavTitle(c.getString(c.getColumnIndex(FAV_TITLE)));
				

				// adding to tags list
				favoriteItems.add(fav);
			} 
			while (c.moveToNext());
		}
		return  favoriteItems;
	}
	
	public Cursor getAllData()
	{
		
		String a[]=new String[]{KEY_ID,FAV_ID,FAV_TITLE,FAV_DESC,FAV_IMAGE};
		//String buildSQL = "SELECT * FROM " + TABLE_FAVORITE;
		Cursor c=sqLiteDatabase.query(TABLE_FAVORITE ,a,null,null,null,null,null);
	
	  
	    return c;
	}
	public int getAllFavCount()
	{
		Cursor c=sqLiteDatabase.rawQuery("SELECT  * FROM " + TABLE_FAVORITE,null);
		int b=c.getCount();
		  
	    return b;
    }
	
	public int getAllArtCount()
	{
		Cursor c=sqLiteDatabase.rawQuery("SELECT  * FROM " + TABLE_ARTICLE,null);
		int b=c.getCount();
		  
	    return b;
    }
	
	

	/**
	 * get datetime
	 * */
	private String getDateTime()
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}

	
	
	
}
