package de.jdsoft.law.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.jdsoft.law.LawListActivity;
import de.jdsoft.law.data.helper.Law;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Favorites implements Constants {

    static public boolean isFav(String id) {
        SQLiteDatabase db = LawListActivity.db.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FAVS, new String[]{KEY_ID}, KEY_ID + "=?",
                new String[]{id}, null, null, null, null);

        int count = cursor.getCount();
        cursor.close();

        if( count > 0 ) {
            return true;
        } else {
            return false;
        }
    }

    static public void addFav(String id) {
        SQLiteDatabase db = LawListActivity.db.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);

        db.insert(TABLE_FAVS, null, values);
    }

    static public void removeFav(String id) {
        SQLiteDatabase db = LawListActivity.db.getWritableDatabase();
        db.delete(TABLE_FAVS, KEY_ID + " = ?",
                new String[] { id });
    }

    public static List<Law> getFavLaws() {
        String selectQuery = "SELECT * FROM " + TABLE_LAWS + " l INNER JOIN " + TABLE_FAVS +
                " f ON l." + KEY_ID + " = f." + KEY_ID;
        SQLiteDatabase db = LawListActivity.db.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        List<Law> result = new ArrayList<Law>();
        Log.e("LawDb", "size " + cursor.getCount());
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
}
