package android.personal.fixtures.database.tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 *
 */
public class Competitions
{
    public static final String TABLE_NAME = "competitions";

    public static final String COL_NAME_FULL_NAME = "full_name";
    public static final String COL_NAME_SHORT_NAME = "short_name";
    public static final String COL_NAME_IS_LEAGUE = "is_league";

    public static final int COL_INDEX_FULL_NAME = 1;
    public static final int COL_INDEX_SHORT_NAME = 2;
    public static final int COL_INDEX_IS_LEAGUE = 3;

    public static final String DEFAULT_SORT_ORDER = COL_NAME_FULL_NAME + " ASC";

    public static void createTable(final SQLiteDatabase database)
    {
        final String createTable = "CREATE TABLE " + TABLE_NAME + "(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY, " +
                COL_NAME_FULL_NAME + " TEXT NOT NULL, " +
                COL_NAME_SHORT_NAME + " TEXT NOT NULL, " +
                COL_NAME_IS_LEAGUE + " INTEGER DEFAULT 0)";

        database.execSQL(createTable);
    }
}
