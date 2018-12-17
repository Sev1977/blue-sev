package android.personal.fixtures.database.tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 *
 */
public class Statistics
{
    public static final String TABLE_NAME = "statistics";

    public static final String COL_NAME_SEASON_ID = "season_id";
    public static final String COL_NAME_GAME_TYPE = "game_type";
    public static final String COL_NAME_FILTER = "filter";
    public static final String COL_NAME_DESCRIPTION = "description";
    public static final String COL_NAME_VALUE = "value";

    public static void createTable(final SQLiteDatabase database)
    {
        final String createTable = "CREATE TABLE " + TABLE_NAME + "(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY, " +
                COL_NAME_SEASON_ID + " INTEGER NOT NULL, " +
                COL_NAME_GAME_TYPE + " TEXT NOT NULL, " +
                COL_NAME_FILTER + " TEXT NOT NULL, " +
                COL_NAME_DESCRIPTION + " TEXT NOT NULL, " +
                COL_NAME_VALUE + " INTEGER DEFAULT 0, " +
                "UNIQUE (" + COL_NAME_SEASON_ID + ", " + COL_NAME_GAME_TYPE + ", " +
                COL_NAME_FILTER + ", " + COL_NAME_DESCRIPTION + ") ON CONFLICT REPLACE, " +
                "FOREIGN KEY (" + COL_NAME_SEASON_ID + ")" +
                "REFERENCES " + Seasons.TABLE_NAME + " (" + BaseColumns._ID + "))";

        database.execSQL(createTable);
    }
}
