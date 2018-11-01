package android.personal.fixtures.database.tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 *
 */
public class Fixtures
{
    public static final String TABLE_NAME = "fixtures";

    public static final String COL_NAME_DATE = "name";
    public static final String COL_NAME_COMPETITION = "competition";
    public static final String COL_NAME_VENUE = "venue";
    public static final String COL_NAME_OPPOSITION = "opposition";
    public static final String COL_NAME_GOALS_SCORED = "goals_scored";
    public static final String COL_NAME_GOALS_CONCEDED = "goals_conceded";
    public static final String COL_NAME_ATTENDANCE = "attendance";
    public static final String COL_NAME_SEASON_ID = "season_id";

    public static final String DEFAULT_SORT_ORDER = COL_NAME_DATE + " ASC";
    public static final String RESULTS_SORT_ORDER = COL_NAME_DATE + " DESC";

    public static final String[] ALL_COLUMNS =
            new String[]{COL_NAME_DATE, COL_NAME_COMPETITION, COL_NAME_VENUE, COL_NAME_OPPOSITION,
                    COL_NAME_GOALS_SCORED, COL_NAME_GOALS_CONCEDED, COL_NAME_ATTENDANCE,
                    COL_NAME_SEASON_ID};

    public static final int COL_ID_DATE = 1;
    public static final int COL_ID_COMPETITION = 2;
    public static final int COL_ID_VENUE = 3;
    public static final int COL_ID_OPPOSITION = 4;
    public static final int COL_ID_GOALS_SCORED = 5;
    public static final int COL_ID_GOALS_CONCEDED = 6;
    public static final int COL_ID_ATTENDANCE = 7;
    public static final int COL_ID_SEASON_ID = 8;

    public static void createTable(final SQLiteDatabase database)
    {
        final String createTable = "CREATE TABLE " + TABLE_NAME + "(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY, " +
                COL_NAME_DATE + " INTEGER NOT NULL, " +
                COL_NAME_COMPETITION + " TEXT, " +
                COL_NAME_VENUE + " TEXT, " +
                COL_NAME_OPPOSITION + " TEXT NOT NULL, " +
                COL_NAME_GOALS_SCORED + " INTEGER DEFAULT 0, " +
                COL_NAME_GOALS_CONCEDED + " INTEGER DEFAULT 0, " +
                COL_NAME_ATTENDANCE + " INTEGER DEFAULT 0, " +
                COL_NAME_SEASON_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + COL_NAME_SEASON_ID + ")" +
                "REFERENCES " + Seasons.TABLE_NAME + " (" + BaseColumns._ID + "))";

        database.execSQL(createTable);
    }
}
