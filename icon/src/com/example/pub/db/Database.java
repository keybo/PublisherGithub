package com.example.pub.db;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database {

	final OpenHelper openHelper;
	static final String DATABASE_NAME = "mydb";
	static final int DATABASE_VERSION = 6;

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

	public static interface Urls
	{
		public static final String NAME="Urls";
		
		
				
	}
	public static interface Project 
	{
		public static final String NAME = "Project";
		public static String C_PROJECTTITLE = "interviewid";
		public static String C_ORGANIZATIONTITLE = "intcategory";
		public static String C_KEYWORD = "interviewtitle";

		public static String C_PROJECTDESCRIPTION = "interviewdescription";
		public static String C_SMALLIMAGE = "interviewimage";

		public static String C_PRICE = "updatedtime";
		public static String C_COUNTRY = "authorname";

		public final static String[] C = new String[] { C_PROJECTTITLE,
				C_ORGANIZATIONTITLE, C_KEYWORD, C_PROJECTDESCRIPTION,
				C_SMALLIMAGE, C_PRICE, C_COUNTRY };
		public final static ColumnsTypes[] CT = new ColumnsTypes[]
				{
				ColumnsTypes.varchar, ColumnsTypes.varchar,
				ColumnsTypes.varchar, ColumnsTypes.varchar,
				ColumnsTypes.varchar, ColumnsTypes.varchar,
				ColumnsTypes.varchar };
		public final static boolean[] CN = new boolean[] { false, false, false,
				false, false, false, false };
		public final static String[] CS = new String[] { COLLATE, COLLATE,
				COLLATE, COLLATE, COLLATE, COLLATE, COLLATE };
	}

	public static interface Projectone {
		public static final String NAME = "Projectone";
		public static String C_PRIMARYKEY = "primarykey";
		public static String C_FAVORITE = "favorite";
		public final static String[] CSOne = new String[] { COLLATE, COLLATE };
		public final static String[] CONE = new String[] { C_PRIMARYKEY,
				C_FAVORITE };
		public final static boolean[] CNOne = new boolean[] { false, false };
		public final static ColumnsTypes[] CTOne = new ColumnsTypes[] {
				ColumnsTypes.varchar, ColumnsTypes.varchar };

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

		public String CreateStatment() {
			StringBuilder sb = new StringBuilder("CREATE TABLE ");
			sb.append(name);
			sb.append(" ([");
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
			sb.append("_id] INTEGER PRIMARY KEY AUTOINCREMENT);");
			return sb.toString();
		}

		public final static Map<String, Tables> AllTables;
		static {
			HashMap<String, Tables> aMap = new HashMap<String, Tables>();
			aMap.put(Project.NAME, new Tables(Project.NAME, Project.C,
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
