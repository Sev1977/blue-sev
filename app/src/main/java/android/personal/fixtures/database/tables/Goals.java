package android.personal.fixtures.database.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.personal.fixtures.EditGoalScorersActivity;
import android.personal.fixtures.database.Database;
import android.personal.fixtures.database.helpers.PlayersHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Goals
{
    public static final String TABLE_NAME = "goals";

    public static final String COL_NAME_FIXTURE_ID = "fixture_id";
    public static final String COL_NAME_PLAYER_ID = "player_id";
    public static final String COL_NAME_PENALTY = "penalty";

    public static final int COL_ID_FIXTURE_ID = 1;
    public static final int COL_ID_PLAYER_ID = 2;
    public static final int COL_ID_PENALTY = 3;

    public static void createTable(final SQLiteDatabase database)
    {
        final String createTable = "CREATE TABLE " + TABLE_NAME + "(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY, " +
                COL_NAME_FIXTURE_ID + " INTEGER NOT NULL, " +
                COL_NAME_PLAYER_ID + " INTEGER NOT NULL, " +
                COL_NAME_PENALTY + " INTEGER DEFAULT 0, " +
                "FOREIGN KEY (" + COL_NAME_FIXTURE_ID + ")" +
                "REFERENCES " + Fixtures.TABLE_NAME + " (" + BaseColumns._ID + "), " +
                "FOREIGN KEY (" + COL_NAME_PLAYER_ID + ")" +
                "REFERENCES " + Players.TABLE_NAME + " (" + BaseColumns._ID + "))";

        database.execSQL(createTable);
    }
}
