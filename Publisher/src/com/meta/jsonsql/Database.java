package com.meta.jsonsql;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database {

	final OpenHelper openHelper;
	static final String DATABASE_NAME = "Publisher.db";
	static final int DATABASE_VERSION = 1;
	public static final String NAME = "Project";

	public static String C_ARTICLETITLE = "articletitle";
	public static String C_ARTICLEID = "articleid";
	public static String C_ARTICLEDESCRIPTION = "articledescription";
	public static String C_ARTICLEIMAGE = "articleimage";
	public static String C_ARTCATEGORY = "artcategory";
	public static String C_AUTHORNAME = "authorname";
	public static String C_UPDATEDTIME = "updatedtime";

	public Database(Context context) throws Exception {
		openHelper = new OpenHelper(context);
	}

	public void Destroy() {
		openHelper.close();
	}

	public static enum ColumnsTypes {
		integer, varchar, guid, datetime, numeric
	};

	static final String COLLATE = "COLLATE LOCALIZED";
	static final String EMPTY = "";

	public static interface Project {

		// public static String C_UPDATEDTIME = "updatedtime";

		public final static String[] C = new String[] { C_ARTICLETITLE,
				C_ARTICLEID, C_ARTICLEDESCRIPTION, C_ARTICLEIMAGE,
				C_ARTCATEGORY, C_AUTHORNAME, C_UPDATEDTIME };
		public final static ColumnsTypes[] CT = new ColumnsTypes[] {

		ColumnsTypes.varchar, ColumnsTypes.varchar, ColumnsTypes.varchar,
				ColumnsTypes.varchar, ColumnsTypes.varchar,
				ColumnsTypes.varchar, ColumnsTypes.varchar };
		public final static boolean[] CN = new boolean[] { false, false, false,
				false, false, false, false };
		public final static String[] CS = new String[] { COLLATE, COLLATE,
				COLLATE, COLLATE, COLLATE, COLLATE, COLLATE };
	}

	public static class Tables {
		String[] columns = null;
		ColumnsTypes[] columnsTypes = null;
		String[] columnsSpecials = null;
		String name = null;
		boolean[] columnsNullable = null;

		Tables(String name, String[] columns, ColumnsTypes[] columnsTypes,
				String[] columnsSpecials, boolean[] columnsNullable) {
			this.name = name;
			this.columns = columns;
			this.columnsTypes = columnsTypes;
			this.columnsSpecials = columnsSpecials;
			this.columnsNullable = columnsNullable;
		}

		public String DropStatment() {
			return "DROP TABLE IF EXISTS " + name;
		}

		public void DeleteAll(SQLiteDatabase db) {
			db.delete(name, null, null);
		}

		public long InsertJSON(SQLiteDatabase db, JSONObject obj)
				throws JSONException {
			ContentValues vals = new ContentValues();
			for (String col : columns) {
				vals.put(col, obj.getString(col));
			}
			return db.insert(name, null, vals);
		}

		public Cursor getAllData(SQLiteDatabase db) {

			String a[] = new String[] { C_ARTICLETITLE, C_ARTICLEID,
					C_ARTICLEDESCRIPTION, C_ARTICLEIMAGE };
			// String buildSQL = "SELECT * FROM " + TABLE_FAVORITE;
			Cursor c = db.query(NAME, a, null, null, null, null, null);

			return c;
		}

		public String CreateStatment() {
			StringBuilder sb = new StringBuilder("CREATE TABLE ");
			sb.append(name);
			sb.append(" ([_id] integer primary key autoincrement, [");
			for (int i = 0; i < columns.length; i++) {
				sb.append(columns[i]);
				sb.append("] ");
				sb.append(columnsTypes[i].name());
				sb.append(' ');
				sb.append(columnsSpecials[i]);
				if (!columnsNullable[i])
					sb.append(" NOT NULL ");
				sb.append(", [");
			}
			sb.append("] );");
			return sb.toString();
		}

		public final static Map<String, Tables> AllTables;
		static 
		{
			HashMap<String, Tables> aMap = new HashMap<String, Tables>();
			aMap.put(Database.NAME, new Tables(Database.NAME, Project.C,
					Project.CT, Project.CS, Project.CN));
			AllTables = Collections.unmodifiableMap(aMap);
		}
	}

	public static class OpenHelper extends SQLiteOpenHelper {

		public OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				for (Tables table : Tables.AllTables.values()) {
					String create = table.CreateStatment();
					db.execSQL(create);
				}
			} catch (Exception e) {
				Log.e("Exception", e.toString());
			}
		}

		public OpenHelper Recreate() {
			onUpgrade(getWritableDatabase(), 1, 2);
			return this;
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			for (Tables table : Tables.AllTables.values()) {
				db.execSQL(table.DropStatment());
			}
			onCreate(db);
		}

	}
}