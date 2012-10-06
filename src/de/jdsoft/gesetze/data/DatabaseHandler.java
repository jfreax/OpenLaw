package de.jdsoft.gesetze.data;

import java.util.List;

import de.jdsoft.gesetze.data.helper.Law;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "gesetze";
	private static final String TABLE_LAWS = "gesetze";

	private static final String KEY_ID = "id";
	private static final String KEY_SHORT_NAME = "short name";
	private static final String KEY_LONG_NAME = "long name";


	DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}


	public void onCreate(SQLiteDatabase db) {
		String CREATE_LAW_TABLE = "CREATE TABLE " + TABLE_LAWS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_SHORT_NAME + " TEXT,"
				+ KEY_LONG_NAME + " TEXT" + ")";
		db.execSQL(CREATE_LAW_TABLE);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LAWS);
		onCreate(db);
	}
	
	
	public void addLaw(Law law) {
		
	}
	
	public Law getLaw() {
		return null;
	}

	public List<Law> getAllLaws() {
		return null;
	}
	
	public int getLawsCount() {
		return 0;
	}
	
	public int updateLaw(Law contact) {
		return 0;
	}
	 
	public void deleteLaw(Law contact) {
		
	}
}
