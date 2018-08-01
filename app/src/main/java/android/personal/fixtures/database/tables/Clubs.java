package android.personal.fixtures.database.tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 *
 */
public class Clubs
{
    public static final String TABLE_NAME = "clubs";

    public static final String COL_NAME_FULL_NAME = "full_name";
    public static final String COL_NAME_SHORT_NAME = "short_name";
    public static final String COL_NAME_CODE = "code";
    public static final String COL_NAME_IS_LEAGUE = "is_league_opponent";

    public static final String DEFAULT_SORT_ORDER = COL_NAME_FULL_NAME + " ASC";

    public static void createTable(final SQLiteDatabase database)
    {
        final String createTable = "CREATE TABLE " + TABLE_NAME + "(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY, " +
                COL_NAME_FULL_NAME + " TEXT NOT NULL, " +
                COL_NAME_SHORT_NAME + " TEXT NOT NULL, " +
                COL_NAME_CODE + " TEXT NOT NULL, " +
                COL_NAME_IS_LEAGUE + " INTEGER NOT NULL DEFAULT 1)";

        database.execSQL(createTable);
    }
}
