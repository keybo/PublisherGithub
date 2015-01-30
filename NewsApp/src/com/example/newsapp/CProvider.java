package com.example.newsapp;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class CProvider extends ContentProvider {

	static final int ARTICLES = 1;
	static final int ARTICLES_ID = 2;
	static final int INTERVIEW = 3;
	static final int INTERVIEW_ID = 4;
	static final int URL = 5;
	static final int URL_ID = 6;
	DBHelper dbHelper;
	Context context;
	SQLiteDatabase sql;

	public static HashMap<String, String> map, map1, map2;
	static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(DbData.AUTHORITY, "/article", ARTICLES);
		uriMatcher.addURI(DbData.AUTHORITY, "/article/#", ARTICLES_ID);
		uriMatcher.addURI(DbData.AUTHORITY, "/interview", INTERVIEW);
		uriMatcher.addURI(DbData.AUTHORITY, "/interview/#", INTERVIEW_ID);
		uriMatcher.addURI(DbData.AUTHORITY, "/url", URL);
		uriMatcher.addURI(DbData.AUTHORITY, "/url/#", URL_ID);

	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count = 0;

		switch (uriMatcher.match(uri)) {
		case ARTICLES:
			count = sql.delete(DbData.FeedColumnsone.TABLE_NAME, selection,
					selectionArgs);
			break;
		case ARTICLES_ID:
			String id = uri.getPathSegments().get(1);
			count = sql.delete(
					DbData.FeedColumnsone.TABLE_NAME,
					DbData.FeedColumnsone._ID
							+ " = "
							+ id
							+ (!TextUtils.isEmpty(selection) ? " AND ("
									+ selection + ')' : ""), selectionArgs);
			break;
		case INTERVIEW:
			count = sql.delete(DbData.FeedColumnstwo.TABLE_NAME, selection,
					selectionArgs);
			break;
		case INTERVIEW_ID:
			String id11 = uri.getPathSegments().get(1);
			count = sql.delete(
					DbData.FeedColumnstwo.TABLE_NAME,
					DbData.FeedColumnstwo._ID
							+ " = "
							+ id11
							+ (!TextUtils.isEmpty(selection) ? " AND ("
									+ selection + ')' : ""), selectionArgs);
			break;
		case URL:
			count = sql.delete(DbData.UrlColumns.TABLE_NAME, selection,
					selectionArgs);
			break;
		case URL_ID:
			String id1 = uri.getPathSegments().get(1);
			count = sql.delete(
					DbData.UrlColumns.TABLE_NAME,
					DbData.UrlColumns._ID
							+ " = "
							+ id1
							+ (!TextUtils.isEmpty(selection) ? " AND ("
									+ selection + ')' : ""), selectionArgs);

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		context.getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {

		switch (uriMatcher.match(uri)) {
		/**
		 * Get all student records
		 */
		case ARTICLES:
			return "vnd.android.cursor.dir/vnd.example.article";

		case ARTICLES_ID:
			return "vnd.android.cursor.item/vnd.example.article";
		case INTERVIEW:
			return "vnd.android.cursor.dir/vnd.example.interview";
		case INTERVIEW_ID:
			return "vnd.android.cursor.item/vnd.example.interview";
		case URL:
			return "vnd.android.cursor.dir/vnd.example.url";
		case URL_ID:
			return "vnd.android.cursor.item/vnd.example.url";

		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	@Override
	public boolean onCreate() {
		context = getContext();
		dbHelper = new DBHelper(context);
		sql = dbHelper.getWritableDatabase();
		return (sql == null) ? false : true;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		long artID = sql.insert(DbData.FeedColumnsone.TABLE_NAME, "", values);
		Uri _uri, _uri1, uri2;
		if (artID > -1) {
			_uri = ContentUris.withAppendedId(DbData.CONTENT_URI, artID);
			context.getContentResolver().notifyChange(_uri, null);
			return _uri;
		}

		long intID = sql.insert(DbData.FeedColumnstwo.TABLE_NAME, "", values);

		/*if (intID > -1) {
			_uri1 = ContentUris.withAppendedId(DbData.CONTENT_URI1, intID);
			context.getContentResolver().notifyChange(_uri1, null);
			return _uri1;
		}
		long urlID = sql.insert(DbData.UrlColumns.TABLE_NAME, "", values);
		if (urlID > -1) {
			uri2 = ContentUris.withAppendedId(DbData.CONTENT_URI1, urlID);
			context.getContentResolver().notifyChange(uri2, null);
			return uri2;
		}*/
		throw new SQLException("Failed to add a record into " + uri);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb, qb1, qb2;
		qb = new SQLiteQueryBuilder();

		qb.setTables(DbData.FeedColumnsone.TABLE_NAME);
		qb1 = new SQLiteQueryBuilder();
		qb1.setTables(DbData.FeedColumnstwo.TABLE_NAME);
		qb2 = new SQLiteQueryBuilder();
		qb2.setTables(DbData.UrlColumns.TABLE_NAME);

		int option = uriMatcher.match(uri);

		switch (option) {
		case ARTICLES:
			qb.setProjectionMap(map);
			break;
		case ARTICLES_ID:
			qb.appendWhere(DbData.FeedColumnsone._ID + "="
					+ uri.getLastPathSegment());
			break;
		case INTERVIEW:
			qb1.setProjectionMap(map1);
			break;
		case INTERVIEW_ID:
			qb1.appendWhere(DbData.FeedColumnstwo._ID + "="
					+ uri.getLastPathSegment());
			break;
		case URL:
			qb2.setProjectionMap(map2);
			break;
		case URL_ID:
			qb2.appendWhere(DbData.UrlColumns._ID + "="
					+ uri.getLastPathSegment());
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		if (sortOrder == null || sortOrder == "") {
			sortOrder = DbData.FeedColumnsone._ID;
		} else if (sortOrder == null || sortOrder == "") {
			sortOrder = DbData.FeedColumnstwo._ID;
		}
		else if(sortOrder==null || sortOrder == "")
		{
			sortOrder=DbData.UrlColumns._ID;
		}

		Cursor c = qb.query(sql, projection, selection, selectionArgs, null,
				null, sortOrder);
		c.setNotificationUri(context.getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int count = 0;

		switch (uriMatcher.match(uri)) {
		case ARTICLES:
			count = sql.update(DbData.FeedColumnsone.TABLE_NAME, values,
					selection, selectionArgs);
			break;
		case ARTICLES_ID:
			count = sql.update(
					DbData.FeedColumnsone.TABLE_NAME,
					values,
					DbData.FeedColumnsone._ID
							+ " = "
							+ uri.getLastPathSegment()
							+ (!TextUtils.isEmpty(selection) ? " AND ("
									+ selection + ')' : ""), selectionArgs);
			break;
		case INTERVIEW:
			count = sql.update(DbData.FeedColumnstwo.TABLE_NAME, values,
					selection, selectionArgs);
			break;
		case INTERVIEW_ID:
			count = sql.update(
					DbData.FeedColumnstwo.TABLE_NAME,
					values,
					DbData.FeedColumnstwo._ID
							+ " = "
							+ uri.getLastPathSegment()
							+ (!TextUtils.isEmpty(selection) ? " AND ("
									+ selection + ')' : ""), selectionArgs);
			break;
		case URL:
			count = sql.update(DbData.UrlColumns.TABLE_NAME, values,
					selection, selectionArgs);
			break;
		case URL_ID:
			count = sql.update(
					DbData.UrlColumns.TABLE_NAME,
					values,
					DbData.UrlColumns._ID
							+ " = "
							+ uri.getLastPathSegment()
							+ (!TextUtils.isEmpty(selection) ? " AND ("
									+ selection + ')' : ""), selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		context.getContentResolver().notifyChange(uri, null);
		return count;
	}

}
