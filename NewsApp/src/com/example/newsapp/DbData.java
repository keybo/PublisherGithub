package com.example.newsapp;

import android.net.Uri;
import android.provider.BaseColumns;

public class DbData {
	public static final String CONTENT = "content://";
	public static final String AUTHORITY = "com.example.provider.Publisher";

	public static final String CONTENT_AUTHORITY = CONTENT + AUTHORITY
			+ "/article";
	
	public static final Uri CONTENT_URI = Uri.parse(CONTENT_AUTHORITY);
	//public static final Uri CONTENT_URI1 = Uri.parse(CONTENT_AUTHORITY1);
	//public static final Uri CONTENT_URI2=Uri.parse(CONTENT_AUTHORITY2);
	static final String TYPE_PRIMARY_KEY = "INTEGER PRIMARY KEY AUTOINCREMENT";

	static final String TYPE_EXTERNAL_ID = "INTEGER(7)";
	static final String TYPE_TEXT = " TEXT not null";
	static final String TYPE_TEXT_UNIQUE = "TEXT UNIQUE";
	static final String TYPE_DATE_TIME = "DATETIME";
	static final String TYPE_INT = "INT";
	static final String TYPE_BOOLEAN = "INTEGER(1)";

	public static class UrlColumns implements BaseColumns {
		public static final String TABLE_NAME = "urls";
		public static final String LINK = "link";

		public static final String[][] COLUMNS = new String[][] {
				{ _ID, TYPE_PRIMARY_KEY }, { LINK, TYPE_TEXT } };

	}

	public static class FeedColumnsone implements BaseColumns {

		public static final String TABLE_NAME = "article";
		public static final String ART_DESC = "article_desc";
		public static final String ART_TITLE = "article_title";
		public static final String ART_AUTHOR = "article_author";
		public static final String ART_TIME = "article_date";
		public static final String ART_IMAGE = "artilce_image";
		public static final String[][] COLUMNS = new String[][] {
				{ _ID, TYPE_PRIMARY_KEY }, { ART_DESC, TYPE_TEXT },
				{ ART_TITLE, TYPE_TEXT }, { ART_AUTHOR, TYPE_TEXT },
				{ ART_TIME, TYPE_TEXT }, { ART_IMAGE, TYPE_TEXT } };
	}

	public static class FeedColumnstwo implements BaseColumns {
		public static final String TABLE_NAME = "interview";
		public static final String INT_DESC = "int_desc";
		public static final String INT_TITLE = "int_title";
		public static final String INT_AUTHOR = "int_author";
		public static final String INT_TIME = "int_date";
		public static final String INT_IMAGE = "int_image";
		public static final String[][] COLUMNS = new String[][] {
				{ _ID, TYPE_PRIMARY_KEY }, { INT_DESC, TYPE_TEXT},
				{ INT_TITLE, TYPE_TEXT }, { INT_AUTHOR, TYPE_TEXT },
				{ INT_TIME, TYPE_TEXT }, { INT_IMAGE, TYPE_TEXT } };

	}

}
