package de.jdsoft.law.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavoriteDb extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "law";
    private static final String TABLE_NAME = "law_fav";

    private static final String KEY_ID = "id";
    private static final String KEY_SLUG = "slug";


    public FavoriteDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public void onCreate(SQLiteDatabase db) {
        String CREATE_LAW_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_SLUG + " TEXT" + ")";
        db.execSQL(CREATE_LAW_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void clear() {
        SQLiteDatabase db = this.getWritableDatabase();
        this.onUpgrade(db, 0, DATABASE_VERSION);
    }

}
