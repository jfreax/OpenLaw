package de.jdsoft.law.database;

public interface Constants {

    public static final int DATABASE_VERSION = 22;
    public static final String DATABASE_NAME = "law";

    public static final String TABLE_LAWS = "law";
    public static final String TABLE_FAVS = "law_fav";

    public static final String KEY_ID = "id";
    public static final String KEY_SHORT_NAME = "shortname";
    public static final String KEY_LONG_NAME = "longname";
    public static final String KEY_SLUG = "text";
}
