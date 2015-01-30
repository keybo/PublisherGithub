package com.example.newsapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;

import com.example.newsapp.DbData.FeedColumnsone;
import com.example.newsapp.DbData.FeedColumnstwo;
import com.example.newsapp.DbData.UrlColumns;

public class DBHelper extends SQLiteOpenHelper {

	Handler mHandler;
	private static final String DATABASE_NAME = "Publisher";
	private static final int DATABASE_VERSION = 1;

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// mHandler = handler;

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(createTable(FeedColumnsone.TABLE_NAME,
				FeedColumnsone.COLUMNS));
		db.execSQL(createTable(FeedColumnstwo.TABLE_NAME,
				FeedColumnstwo.COLUMNS));
		db.execSQL(createTable(UrlColumns.TABLE_NAME,
				UrlColumns.COLUMNS));

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		db.execSQL("DROP TABLE IF EXISTS " + FeedColumnsone.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + FeedColumnstwo.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " +UrlColumns.TABLE_NAME);
		onCreate(db);
	}

	private String createTable(String tableName, String[][] columns) {
		if (tableName == null || columns == null || columns.length == 0) {
			throw new IllegalArgumentException(
					"Invalid parameters for creating table " + tableName);
		} else {
			StringBuilder stringBuilder = new StringBuilder("CREATE TABLE ");

			stringBuilder.append(tableName);
			stringBuilder.append(" (");
			for (int n = 0, i = columns.length; n < i; n++) {
				if (n > 0) {
					stringBuilder.append(", ");
				}
				stringBuilder.append(columns[n][0]).append(' ')
						.append(columns[n][1]);
			}
			return stringBuilder.append(");").toString();
		}
	}
}
