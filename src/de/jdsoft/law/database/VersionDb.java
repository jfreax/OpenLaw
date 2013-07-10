package de.jdsoft.law.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class VersionDb extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 4;
	private static final String DATABASE_NAME = "law";
	private static final String TABLE_NAME = "version";

	private static final String KEY_ID = "id";
	private static final String KEY_VERSION = "version";


	public VersionDb(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}


	public void onCreate(SQLiteDatabase db) {
		String CREATE_LAW_TABLE = "CREATE TABLE " + TABLE_NAME + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," 
				+ KEY_VERSION + " INT" + ")";
		db.execSQL(CREATE_LAW_TABLE);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

	public int getVersion() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, new String[] { KEY_VERSION }, null,
				null, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		cursor.close();
		db.close();
		return Integer.parseInt(cursor.getString(0));
	}



	public int updateLaw(int newVersion) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_VERSION, newVersion);

		int ret =  db.update(TABLE_NAME, values,null, null);
		db.close();

		return ret;
	}
}
