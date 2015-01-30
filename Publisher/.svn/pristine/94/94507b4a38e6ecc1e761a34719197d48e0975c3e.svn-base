package com.meta.jsonsql;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public class Provider extends ContentProvider {

    @Override
    public int delete(Uri arg0, String arg1, String[] arg2) {
        return 0;
    }

    private static final int PROJECTS = 1;
    private static final int PROJECT = 2;
    public static final String PROJECTS_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/Project";
    public static final String PROJECT_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/Project";
    public static final String AUTHORITY = "com.meta.jsonsql";
    public static  Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    static final HashMap<String, String> map = new HashMap<String, String>();
    static 
    {
        matcher.addURI(AUTHORITY, "Project", PROJECTS);
        matcher.addURI(AUTHORITY, "Project/#", PROJECT);
        map.put(BaseColumns._ID, BaseColumns._ID);
        map.put(Database.C_ARTCATEGORY, Database.C_ARTCATEGORY);
        map.put(Database.C_ARTICLEDESCRIPTION, Database.C_ARTICLEDESCRIPTION);
        map.put(Database.C_ARTICLEID, Database.C_ARTICLEID);
       map.put(Database.C_ARTICLEIMAGE,Database.C_ARTICLEIMAGE);
        map.put(Database.C_ARTICLETITLE,Database.C_ARTICLETITLE);
        map.put(Database.C_AUTHORNAME, Database.C_AUTHORNAME);        
        map.put(Database.C_UPDATEDTIME, Database.C_UPDATEDTIME);
      
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
    private Database.OpenHelper mDB;
    @Override
    public Uri insert(Uri arg0, ContentValues arg1)
    {
        // TODO Auto-generated method stub
        return null;
    }
    /*@SuppressWarnings("null")
	@Override
    public Uri insert(Uri arg0, ContentValues values)
    {
    	  long newId;

          int option = matcher.match(arg0);

          SQLiteDatabase database = mDB.getWritableDatabase();
          switch(option)
          {
          case PROJECTS:
        	  
          case PROJECT:
        	  Cursor cursor = null;
        	do
              { 
              	
                 /* values.put(Database.C_ARTCATEGORY, cursor.getString(2));
                  values.put(Database.C_ARTICLEDESCRIPTION,cursor.getString(3));
                  values.put(Database.C_ARTICLEID, cursor.getString(4));
                  values.put(Database.C_ARTICLEIMAGE, cursor.getString(5));
                  values.put(Database.C_ARTICLETITLE, cursor.getString(6));
                  values.put(Database.C_AUTHORNAME, cursor.getString(7));
                 // values.put(Database.C_UPDATEDTIME,cursor.getString(8));
                 
              }
        	while(cursor.moveToFirst()) ; 
        	  cursor.close();
        	  newId=database.insert(Database.NAME, null, values);
           break;
          default:
              throw new IllegalArgumentException("Illegal insert");
          }
                
          if (newId > -1)
          {
              getContext().getContentResolver().notifyChange(arg0, null);
              return ContentUris.withAppendedId(arg0, newId);
          } 
          else
          {
              throw new SQLException("Could not insert row into " + arg0);
          }
    }*/

   

    @Override
    public boolean onCreate() 
    {
        try 
        {
            mDB = new Database.OpenHelper(getContext());
        } 
        catch (Exception e) 
        {
            Log.e("Exception", e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder)
    {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        switch (matcher.match(uri))
        {
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

     public int update(Uri uri, ContentValues cv, String selection, String[] selectionArgs) {
         String table = null;
         SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        switch (matcher.match(uri)) 
        {
         case PROJECTS:
                 table = uri.getPathSegments().get(0);
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
         return mDB.getWritableDatabase().update(table, cv, selection, selectionArgs);
 }


}