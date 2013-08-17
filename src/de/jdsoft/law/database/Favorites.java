package de.jdsoft.law.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import de.jdsoft.law.LawListActivity;
import de.jdsoft.law.data.helper.Law;

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

    static public void toggleFav(String id) {
        if( isFav(id) ) {
            removeFav(id);
        } else {
            addFav(id);
        }
    }
}
