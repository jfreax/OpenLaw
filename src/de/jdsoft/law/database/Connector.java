package de.jdsoft.law.database;

import java.util.ArrayList;
import java.util.List;

import de.jdsoft.law.data.helper.Law;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils.InsertHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Connector extends SQLiteOpenHelper implements Constants {

	public Connector(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void onCreate(SQLiteDatabase db) {
		String CREATE_LAW_TABLE = "CREATE TABLE " + TABLE_LAWS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," 
				+ KEY_SHORT_NAME + " TEXT,"
				+ KEY_LONG_NAME + " TEXT,"
				+ KEY_SLUG + " TEXT" + ")";
		db.execSQL(CREATE_LAW_TABLE);

        String CREATE_FAV_TABLE = "CREATE TABLE " + TABLE_FAVS + "("
                + KEY_ID + " INTEGER PRIMARY KEY)";
        db.execSQL(CREATE_FAV_TABLE);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LAWS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVS);

		onCreate(db);
	}
	
	public void clear() {
		SQLiteDatabase db = this.getWritableDatabase();
		this.onUpgrade(db, 0, DATABASE_VERSION);
	}

}
