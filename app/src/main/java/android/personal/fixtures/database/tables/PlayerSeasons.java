package android.personal.fixtures.database.tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 *
 */
public class PlayerSeasons
{
    public static final String TABLE_NAME = "player_seasons";

    private static final String COL_NAME_PLAYER_ID = "player_id";
    private static final String COL_NAME_SEASON_ID = "season_id";
    private static final String COL_NAME_LEAGUE_GOALS = "league_goals";
    private static final String COL_NAME_FA_CUP_GOALS = "fa_cup_goals";
    private static final String COL_NAME_LEAGUE_CUP_GOALS = "league_cup_goals";
    private static final String COL_NAME_TOTAL_GOALS = "total_goals";

    public static void createTable(final SQLiteDatabase database)
    {
        final String createTable = "CREATE TABLE " + TABLE_NAME + "(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY, " +
                COL_NAME_PLAYER_ID + " INTEGER NOT NULL, " +
                COL_NAME_SEASON_ID + " INTEGER NOT NULL, " +
                COL_NAME_LEAGUE_GOALS + " INTEGER DEFAULT 0, " +
                COL_NAME_FA_CUP_GOALS + " INTEGER DEFAULT 0, " +
                COL_NAME_LEAGUE_CUP_GOALS + " INTEGER DEFAULT 0, " +
                COL_NAME_TOTAL_GOALS + " INTEGER DEFAULT 0, " +
                "FOREIGN KEY (" + COL_NAME_PLAYER_ID + ")" +
                "REFERENCES " + Players.TABLE_NAME + " (" + BaseColumns._ID + "), " +
                "FOREIGN KEY (" + COL_NAME_SEASON_ID + ")" +
                "REFERENCES " + Seasons.TABLE_NAME + " (" + BaseColumns._ID + "))";

        database.execSQL(createTable);
    }
}
