package com.example.pub.provider;

import java.util.HashMap;

import com.example.pub.db.Database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public class LooserProvider extends ContentProvider 
{

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) 
	{
		return 0;
	}

	private static final int PROJECTS = 1;
	private static final int PROJECT = 2;
	public static final String PROJECTS_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/Project";
	public static final String PROJECT_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/Project";
	public static final String AUTHORITY = "com.example.pub";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
	static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
	static final HashMap<String, String> map = new HashMap<String, String>();
	static final HashMap<String, String> map1 = new HashMap<String, String>();
	static
	{
		map1.put(Database.Projectone.C_FAVORITE, Database.Projectone.C_FAVORITE);
		
	}
	
	static {
		matcher.addURI(AUTHORITY, "Project", PROJECTS);
		matcher.addURI(AUTHORITY, "Project/#", PROJECT);
		map.put(BaseColumns._ID, BaseColumns._ID);
		
		map.put(Database.Project.C_COUNTRY, Database.Project.C_COUNTRY);
		
		map.put(Database.Project.C_KEYWORD, Database.Project.C_KEYWORD);
		map.put(Database.Project.C_ORGANIZATIONTITLE,
				Database.Project.C_ORGANIZATIONTITLE);
		map.put(Database.Project.C_PRICE, Database.Project.C_PRICE);
		map.put(Database.Project.C_PROJECTDESCRIPTION,
				Database.Project.C_PROJECTDESCRIPTION);
		map.put(Database.Project.C_PROJECTTITLE,
				Database.Project.C_PROJECTTITLE);
		
		map.put(Database.Project.C_SMALLIMAGE, Database.Project.C_SMALLIMAGE);
	}

	@Override
	public String getType(Uri uri)
	{
		switch (matcher.match(uri))
		{
		case PROJECTS:
			return PROJECTS_MIME_TYPE;
		case PROJECT:
			return PROJECT_MIME_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	private Database.OpenHelper mDB;

	@Override
	public boolean onCreate() {
		try {
			mDB = new Database.OpenHelper(getContext());
		} catch (Exception e) {
			Log.e("Exception", e.getLocalizedMessage());
			return false;
		}
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		switch (matcher.match(uri)) {
		case PROJECTS:
			String table = uri.getPathSegments().get(0);
			builder.setTables(table);
			break;
		case PROJECT:
			table = uri.getPathSegments().get(0);
			builder.setTables(table);
			selection = "_id=?";
			selectionArgs = new String[] { uri.getPathSegments().get(1) };
			break;
		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}
		builder.setProjectionMap(map);

		Cursor cursor = builder.query(mDB.getReadableDatabase(), projection, selection,
				selectionArgs, null, null, sortOrder);

		if (cursor == null) {
			return null;
		}
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

}
