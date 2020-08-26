package android.personal.fixtures.database.tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 *
 */
public class Players
{
    public static final String TABLE_NAME = "players";

    public static final String COL_NAME_FORENAME = "forename";
    public static final String COL_NAME_SURNAME = "surname";
    public static final String COL_NAME_CURRENT = "isCurrent";

    public static final String DEFAULT_SORT_ORDER = COL_NAME_FORENAME + " ASC";
    public static final String NAME_SORT_ORDER = COL_NAME_SURNAME + " ASC";

    public static void createTable(final SQLiteDatabase database)
    {
        final String createTable = "CREATE TABLE " + TABLE_NAME + "(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY, " +
                COL_NAME_FORENAME + " TEXT NOT NULL, " +
                COL_NAME_SURNAME + " TEXT NOT NULL, " +
                COL_NAME_CURRENT + " INTEGER DEFAULT 1)";

        database.execSQL(createTable);
    }
}
