package android.personal.fixtures.database.tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 *
 */
public class Seasons
{
    public static final String TABLE_NAME = "seasons";

    public static final String COL_NAME_NAME = "name";

    public static final int COL_ID_NAME = 1;

    public static final String DEFAULT_SORT_ORDER = COL_NAME_NAME + " ASC";

    public static void createTable(final SQLiteDatabase database)
    {
        final String createTable = "CREATE TABLE " + TABLE_NAME + "(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY, " +
                COL_NAME_NAME + " TEXT NOT NULL)";

        database.execSQL(createTable);
    }
}
