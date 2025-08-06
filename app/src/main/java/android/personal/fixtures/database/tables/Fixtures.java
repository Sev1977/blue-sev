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
    /** Short name of the competition */
    public static final String COL_NAME_COMPETITION = "competition";
    /** "Home" or "Away" */
    public static final String COL_NAME_VENUE = "venue";
    /** Short name of the club */
    public static final String COL_NAME_OPPOSITION = "opposition";
    public static final String COL_NAME_GOALS_SCORED = "goals_scored";
    public static final String COL_NAME_GOALS_CONCEDED = "goals_conceded";
    public static final String COL_NAME_GOALS_SCORED_ET = "goals_scored_et";
    public static final String COL_NAME_GOALS_CONCEDED_ET = "goals_conceded_et";
    public static final String COL_NAME_GOALS_SCORED_PENS = "goals_scored_pens";
    public static final String COL_NAME_GOALS_CONCEDED_PENS = "goals_conceded_pens";
    public static final String COL_NAME_ATTENDANCE = "attendance";
    public static final String COL_NAME_SEASON_ID = "season_id";

    public static final String DEFAULT_SORT_ORDER = COL_NAME_DATE + " ASC";
    public static final String RESULTS_SORT_ORDER = COL_NAME_DATE + " DESC";

    public static final int COL_ID_DATE = 1;
    public static final int COL_ID_COMPETITION = 2;
    public static final int COL_ID_VENUE = 3;
    public static final int COL_ID_OPPOSITION = 4;
    public static final int COL_ID_GOALS_SCORED = 5;
    public static final int COL_ID_GOALS_CONCEDED = 6;
    public static final int COL_ID_GOALS_SCORED_ET = 7;
    public static final int COL_ID_GOALS_CONCEDED_ET = 8;
    public static final int COL_ID_GOALS_SCORED_PENS = 9;
    public static final int COL_ID_GOALS_CONCEDED_PENS = 10;
    public static final int COL_ID_ATTENDANCE = 11;
    public static final int COL_ID_SEASON_ID = 12;

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
                COL_NAME_GOALS_SCORED_ET + " INTEGER DEFAULT -1, " +
                COL_NAME_GOALS_CONCEDED_ET + " INTEGER DEFAULT -1, " +
                COL_NAME_GOALS_SCORED_PENS + " INTEGER DEFAULT -1, " +
                COL_NAME_GOALS_CONCEDED_PENS + " INTEGER DEFAULT -1, " +
                COL_NAME_ATTENDANCE + " INTEGER DEFAULT 0, " +
                COL_NAME_SEASON_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + COL_NAME_SEASON_ID + ")" +
                "REFERENCES " + Seasons.TABLE_NAME + " (" + BaseColumns._ID + "))";

        database.execSQL(createTable);
    }
}
