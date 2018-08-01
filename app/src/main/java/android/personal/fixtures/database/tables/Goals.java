package android.personal.fixtures.database.tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 *
 */
public class Goals
{
    public static final String TABLE_NAME = "goals";

    public static final String COL_NAME_FIXTURE_ID = "fixture_id";
    public static final String COL_NAME_PLAYER_NAME = "player_name";
    public static final String COL_NAME_PENALTY = "penalty";

    public static void createTable(final SQLiteDatabase database)
    {
        final String createTable = "CREATE TABLE " + TABLE_NAME + "(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY, " +
                COL_NAME_FIXTURE_ID + " INTEGER NOT NULL, " +
                COL_NAME_PLAYER_NAME + " TEXT NOT NULL, " +
                COL_NAME_PENALTY + " INTEGER DEFAULT 0, " +
                "FOREIGN KEY (" + COL_NAME_FIXTURE_ID + ")" +
                "REFERENCES " + Fixtures.TABLE_NAME + " (" + BaseColumns._ID + "))";

        database.execSQL(createTable);
    }
}
