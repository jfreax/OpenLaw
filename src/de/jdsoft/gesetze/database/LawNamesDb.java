package de.jdsoft.gesetze.database;

import java.util.ArrayList;
import java.util.List;

import de.jdsoft.gesetze.data.helper.Law;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils.InsertHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LawNamesDb extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 17;
	private static final String DATABASE_NAME = "gesetze";
	private static final String TABLE_LAWS = "gesetze";

	private static final String KEY_ID = "id";
	private static final String KEY_SHORT_NAME = "shortname";
	private static final String KEY_LONG_NAME = "longname";
	private static final String KEY_SLUG = "text";


	public LawNamesDb(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}


	public void onCreate(SQLiteDatabase db) {
		String CREATE_LAW_TABLE = "CREATE TABLE " + TABLE_LAWS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," 
				+ KEY_SHORT_NAME + " TEXT,"
				+ KEY_LONG_NAME + " TEXT,"
				+ KEY_SLUG + " TEXT" + ")";
		db.execSQL(CREATE_LAW_TABLE);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LAWS);
		onCreate(db);
	}
	
	public void clear() {
		SQLiteDatabase db = this.getWritableDatabase();
		this.onUpgrade(db, 0, DATABASE_VERSION);
	}


	public void addLaw(Law law) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		values.put(KEY_SHORT_NAME, law.getShortName());
		values.put(KEY_LONG_NAME, law.getLongName());
		values.put(KEY_SLUG, law.getSlug());

		db.insert(TABLE_LAWS, null, values);
		db.close();
	}

	public void addLaws(List<Law> laws) {
		SQLiteDatabase db = this.getWritableDatabase();
		InsertHelper iHelp = new InsertHelper(db, TABLE_LAWS);

		// Get the indices you need to bind data to
		// Similar to Cursor.getColumnIndex("col_name");                 
		int shortIndex = iHelp.getColumnIndex(KEY_SHORT_NAME);
		int longIndex = iHelp.getColumnIndex(KEY_LONG_NAME);
		int slugIndex = iHelp.getColumnIndex(KEY_SLUG);

		try {
			db.beginTransaction();
			for( Law law : laws) {
				// Need to tell the helper you are inserting (rather than replacing)
				iHelp.prepareForInsert();

				// Equivalent to ContentValues.put("field","value") 
				iHelp.bind(shortIndex, law.getShortName());
				iHelp.bind(longIndex, law.getLongName());
				iHelp.bind(slugIndex, law.getSlug());

				// The db.insert() equivalent
				iHelp.execute();
			}
			db.setTransactionSuccessful();
		}
		finally {
			db.endTransaction();
		}
		db.close();
	}

	public Law getLaw(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_LAWS, new String[] { KEY_ID,
				KEY_SHORT_NAME, KEY_LONG_NAME, KEY_SLUG }, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor.getCount() == 0 ) {
			Log.e(LawNamesDb.class.getName(), "No db entry for id "+id);
			return null;
		}
		
		if (cursor != null)
			cursor.moveToFirst();
		Law law = new Law(Integer.parseInt(cursor.getString(0)),
				cursor.getString(1), cursor.getString(3), cursor.getString(2));

		cursor.close();
		db.close();
		return law;
	}


	public Law getLaw(String shortName) {	
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_LAWS, new String[] { KEY_ID,
				KEY_SHORT_NAME, KEY_LONG_NAME, KEY_SLUG }, KEY_SHORT_NAME + "=?",
				new String[] { shortName }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		Law law = new Law(Integer.parseInt(cursor.getString(0)),
				cursor.getString(1), cursor.getString(3), cursor.getString(2));

		cursor.close();
		db.close();
		return law;
	}

	/**
	 * Do not forget to close the cursor!
	 * @return
	 */
	public List<Law> getAllLaws() {
		String selectQuery = "SELECT * FROM " + TABLE_LAWS + " ORDER BY " + KEY_SHORT_NAME + " ASC";
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.rawQuery(selectQuery, null);
		List<Law> result = new ArrayList<Law>();
		Log.e("LawDb", "size "+cursor.getCount());
		if (cursor.moveToFirst()) { 
			do {
				Law law = new Law();
				law.setID(Integer.parseInt(cursor.getString(0)));
				law.setShortName(cursor.getString(1));
				law.setLongName(cursor.getString(2));
				law.setSlug(cursor.getString(3));

				result.add(law);
			} while (cursor.moveToNext());
		}
		
		cursor.close();
		db.close();

		return result;
	}

	public int getLawsCount() {
		String countQuery = "SELECT  * FROM " + TABLE_LAWS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		int count = cursor.getCount();
		cursor.close();
		db.close();

		return count;
	}

	public int updateLaw(Law law) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_SHORT_NAME, law.getShortName());
		values.put(KEY_LONG_NAME, law.getLongName());
		values.put(KEY_SLUG, law.getSlug());

		int ret =  db.update(TABLE_LAWS, values, KEY_ID + " = ?",
				new String[] { String.valueOf(law.getID()) });
		db.close();

		return ret;
	}

	public void deleteLaw(Law law) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_LAWS, KEY_ID + " = ?",
				new String[] { String.valueOf(law.getID()) });
		db.close();
	}
}
