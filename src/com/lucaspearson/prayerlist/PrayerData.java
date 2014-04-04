package com.lucaspearson.prayerlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class PrayerData {
	PrayerDataHelper helper;

	public PrayerData(Context c) {
		helper = new PrayerDataHelper(c);
	}

	public long insertData(String name, String description, int priority, String category) {
		ContentValues cv = new ContentValues();
		cv.put(PrayerDataHelper.KEY_NAME, name);
		cv.put(PrayerDataHelper.KEY_DESCRIPTION, description);
		cv.put(PrayerDataHelper.KEY_PRIORITY, priority);
		cv.put(PrayerDataHelper.KEY_CATEGORY, category);
		long id = -1;
		try {
			SQLiteDatabase db = helper.getWritableDatabase();
			id = db.insert(PrayerDataHelper.DATABASE_TABLE, null, cv);
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("SQL ERROR", e.getMessage().toString());
		}
		return id;
	}

	public Cursor returnAllCursor() {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT * from "+PrayerDataHelper.DATABASE_TABLE, null);
		return c;

	}
	public Cursor returnAllCursorByPriority() {
		
		//db.query does an ORDER BY KEY_PRIORITY with DESC for descending because higher needs to come first
		String[] columns = {PrayerDataHelper.KEY_ROWID, PrayerDataHelper.KEY_NAME, PrayerDataHelper.KEY_PRIORITY, PrayerDataHelper.KEY_CATEGORY, PrayerDataHelper.KEY_DESCRIPTION};
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = db.query(PrayerDataHelper.DATABASE_TABLE, columns, null, null, null, null, PrayerDataHelper.KEY_PRIORITY + " DESC", null);
		return c;

	}

	public Cursor returnCursorWithID(int id) {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM "
				+ PrayerDataHelper.DATABASE_TABLE + " WHERE "
				+ PrayerDataHelper.KEY_ROWID + " = " + id, null);
		return c;
	}

	public boolean deletePrayerWithID(int id) {
		SQLiteDatabase db = helper.getWritableDatabase();
		String where = PrayerDataHelper.KEY_ROWID + "="+id;
		try {
			db.delete(PrayerDataHelper.DATABASE_TABLE, where, null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public boolean updatePrayerWithID(int id, String name, String description, int priority,
			String category) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(PrayerDataHelper.KEY_NAME, name);
		cv.put(PrayerDataHelper.KEY_DESCRIPTION, description);
		cv.put(PrayerDataHelper.KEY_PRIORITY, priority);
		cv.put(PrayerDataHelper.KEY_CATEGORY, category);
		try {
			db.update(PrayerDataHelper.DATABASE_TABLE, cv,
					PrayerDataHelper.KEY_ROWID + "=" + id, null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	private class PrayerDataHelper extends SQLiteOpenHelper {
		private static final String KEY_ROWID = "_id";
		private static final String KEY_NAME = "name";
		private static final String KEY_PRIORITY = "priority";
		private static final String KEY_CATEGORY = "category";
		private static final String KEY_DESCRIPTION = "description";
		
		//public static String[] ALL_COLUMNS = {KEY_ROWID, KEY_NAME, KEY_PRIORITY, KEY_CATEGORY, KEY_DESCRIPTION};
		
		private static final String DATABASE_NAME = "prayerdata";
		private static final String DATABASE_TABLE = "PRAYER";
		private static final int DATABASE_VERSION = 1;
		private static final String CREATE_TABLE = "CREATE TABLE "
				+ DATABASE_TABLE + " (" + KEY_ROWID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_NAME
				+ " TEXT, "+KEY_DESCRIPTION +" TEXT, " + KEY_PRIORITY + " INTEGER, " + KEY_CATEGORY + " TEXT);";

		private static final String DROP_TABLE = "DROP " + DATABASE_TABLE + ";";

		public PrayerDataHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			Log.d("PrayerDataHelper", "Made it pass the super call");
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// This is used to create the database this should be one of VERY
			// few
			// execSQL methods called
			try {
				Log.d("ON CREATE", "Got to onCreate in Helper class");
				db.execSQL(CREATE_TABLE);

			} catch (SQLException sqlE) {
				sqlE.printStackTrace();
				Log.e("SQL ERROR", sqlE.getMessage().toString());
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			try {
				db.execSQL(DROP_TABLE);
				onCreate(db);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
