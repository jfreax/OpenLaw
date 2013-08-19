package de.jdsoft.law.database;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import de.jdsoft.law.LawListActivity;
import de.jdsoft.law.data.helper.Law;

import java.util.ArrayList;
import java.util.List;

public class Laws implements Constants {

    static public void clear() {
        SQLiteDatabase db = LawListActivity.db.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LAWS);
        LawListActivity.db.onCreate(db);
    }

    static public void addLaw(Law law) {
        SQLiteDatabase db = LawListActivity.db.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_SHORT_NAME, law.getShortName());
        values.put(KEY_LONG_NAME, law.getLongName());
        values.put(KEY_SLUG, law.getSlug());

        db.insert(TABLE_LAWS, null, values);
    }

    static public void addLaws(List<Law> laws) {
        SQLiteDatabase db = LawListActivity.db.getWritableDatabase();
        DatabaseUtils.InsertHelper iHelp = new DatabaseUtils.InsertHelper(db, TABLE_LAWS);

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
    }

    static public Law getLaw(int id) {
        Cursor cursor;
        Law law = null;

        try {
            SQLiteDatabase db = LawListActivity.db.getReadableDatabase();
            cursor = db.query(TABLE_LAWS, new String[] { KEY_ID,
                    KEY_SHORT_NAME, KEY_LONG_NAME, KEY_SLUG }, KEY_ID + "=?",
                    new String[] { String.valueOf(id) }, null, null, null, null);
            if (cursor.getCount() == 0 ) {
                Log.e(Connector.class.getName(), "No db entry for id " + id);
                return null;
            }

            if (cursor != null)
                cursor.moveToFirst();
            law = new Law(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1), cursor.getString(3), cursor.getString(2));

            cursor.close();
        } catch (SQLiteException e) {
            // FIXME do something... but most likely only the update process is running
        }

        return law;
    }


    static public Law getLaw(String shortName) {
        SQLiteDatabase db = LawListActivity.db.getReadableDatabase();

        Cursor cursor = db.query(TABLE_LAWS, new String[] { KEY_ID,
                KEY_SHORT_NAME, KEY_LONG_NAME, KEY_SLUG }, KEY_SHORT_NAME + "=?",
                new String[] { shortName }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Law law = new Law(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(3), cursor.getString(2));

        cursor.close();
        return law;
    }

    /**
     * Do not forget to close the cursor!
     * @return
     */
    static public List<Law> getAllLaws() {
        String selectQuery = "SELECT * FROM " + TABLE_LAWS + " ORDER BY " + KEY_SHORT_NAME + " COLLATE LOCALIZED ASC";
        SQLiteDatabase db = LawListActivity.db.getReadableDatabase();

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
        return result;
    }

    static public int getLawsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LAWS;
        SQLiteDatabase db = LawListActivity.db.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    static public int updateLaw(Law law) {
        SQLiteDatabase db = LawListActivity.db.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SHORT_NAME, law.getShortName());
        values.put(KEY_LONG_NAME, law.getLongName());
        values.put(KEY_SLUG, law.getSlug());

        int ret =  db.update(TABLE_LAWS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(law.getID()) });

        return ret;
    }

    static public void deleteLaw(Law law) {
        SQLiteDatabase db = LawListActivity.db.getWritableDatabase();
        db.delete(TABLE_LAWS, KEY_ID + " = ?",
                new String[] { String.valueOf(law.getID()) });
    }
}
