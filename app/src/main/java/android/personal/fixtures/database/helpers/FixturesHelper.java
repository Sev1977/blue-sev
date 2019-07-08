package android.personal.fixtures.database.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.personal.fixtures.R;
import android.personal.fixtures.Settings;
import android.personal.fixtures.database.Database;
import android.personal.fixtures.database.tables.Competitions;
import android.personal.fixtures.database.tables.Fixtures;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class FixturesHelper
{
    private static final String TAG = FixturesHelper.class.getSimpleName();

    private static final String[] GOALS_FOR_CONCEDED =
            new String[]{Fixtures.COL_NAME_GOALS_SCORED, Fixtures.COL_NAME_GOALS_CONCEDED};
    private static final String RESULTS_WHERE = Fixtures.COL_NAME_DATE + "<?";
    private static final String FIXTURES_WHERE = Fixtures.COL_NAME_DATE + ">=?";

    private static String getLeagueName(final Database database)
    {
        String name = "";
        final Cursor league = CompetitionsHelper.getAllLeagues(database);
        if (league != null)
        {
            if (league.getCount() > 1)
            {
                Log.w(TAG, "We've got more than 1 league competition, I'm using the first one in" +
                        " the list");
            }
            if (league.moveToFirst())
            {
                name = league.getString(Competitions.COL_INDEX_SHORT_NAME);
            }

            league.close();
        }

        return name;
    }

    public static Cursor getAllResults(final Database database, final Context appContext)
    {
        Log.d(TAG, "getAllResults");
        final long now = System.currentTimeMillis();
        final long nowInSeconds = TimeUnit.MILLISECONDS.toSeconds(now);
        final Cursor allResults = database.getSelection(Fixtures.TABLE_NAME,
                RESULTS_WHERE + " AND " + Fixtures.COL_NAME_SEASON_ID + "=?",
                new String[]{String.valueOf(nowInSeconds),
                        String.valueOf(Settings.getSelectedSeasonId(appContext))},
                Fixtures.RESULTS_SORT_ORDER);
        if (allResults != null)
        {
            allResults.moveToFirst();
        }
        return allResults;
    }

    public static Cursor getLeagueResults(final Database database)
    {
        Log.d(TAG, "getLeagueResults");

        // Now we can get all the league results
        final long now = System.currentTimeMillis();
        final long nowInSeconds = TimeUnit.MILLISECONDS.toSeconds(now);
        final Cursor leagueResults = database.getSelection(Fixtures.TABLE_NAME,
                Fixtures.COL_NAME_DATE + "<? AND " + Fixtures.COL_NAME_COMPETITION + "=?",
                new String[]{String.valueOf(nowInSeconds), getLeagueName(database)},
                Fixtures.RESULTS_SORT_ORDER);
        if (leagueResults != null)
        {
            leagueResults.moveToFirst();
        }
        return leagueResults;
    }

    public static Cursor getAllFixtures(final Database database, final Context appContext)
    {
        Log.d(TAG, "getAllFixtures");

        final long now = System.currentTimeMillis();
        final long nowInSeconds = TimeUnit.MILLISECONDS.toSeconds(now);
        final Cursor allFixtures = database.getSelection(Fixtures.TABLE_NAME, FIXTURES_WHERE + " " +
                        "AND " + Fixtures.COL_NAME_SEASON_ID + "=?",
                new String[]{String.valueOf(nowInSeconds),
                        String.valueOf(Settings.getSelectedSeasonId(appContext))},
                Fixtures.DEFAULT_SORT_ORDER);
        if (allFixtures != null)
        {
            allFixtures.moveToFirst();
            //            DatabaseUtils.dumpCursor(allFixtures);
        }
        return allFixtures;
    }

    /**
     * Get all the league fixtures for the selected season, so get all fixtures where the date is in
     * the future, the season is the selected season and the competition is a league.
     */
    public static Cursor getLeagueFixtures(final Database database, final Context appContext)
    {
        Log.d(TAG, "getLeagueFixtures");

        // Now we can get all the league results
        final long now = System.currentTimeMillis();
        final long nowInSeconds = TimeUnit.MILLISECONDS.toSeconds(now);
        final Cursor leagueFixtures = database.getSelection(Fixtures.TABLE_NAME,
                Fixtures.COL_NAME_DATE + ">? AND " + Fixtures.COL_NAME_SEASON_ID + "=?",
                new String[]{String.valueOf(nowInSeconds),
                        String.valueOf(Settings.getSelectedSeasonId(appContext))},
                Fixtures.DEFAULT_SORT_ORDER);
        if (leagueFixtures != null)
        {
            leagueFixtures.moveToFirst();
        }
        return leagueFixtures;
    }

    public static ArrayList<Integer> getRecentForm(final Database database)
    {
        final ArrayList<Integer> form = new ArrayList<>();

        final long now = System.currentTimeMillis();
        final long nowInSeconds = TimeUnit.MILLISECONDS.toSeconds(now);
        final Cursor cursor = database.getReadableDatabase().query(Fixtures.TABLE_NAME,
                GOALS_FOR_CONCEDED, RESULTS_WHERE, new String[]{String.valueOf(nowInSeconds)}, null,
                null, Fixtures.COL_NAME_DATE + " DESC LIMIT 6");
        if (cursor != null)
        {
            if (cursor.moveToFirst())
            {
                do
                {
                    final int scored = cursor.getInt(0);
                    final int conceded = cursor.getInt(1);

                    // if goals scored is > goals conceded, then it's a win
                    if (scored > conceded)
                    {
                        form.add(3);
                    }
                    // else if goals scored == goals conceded, then it's a draw
                    else if (scored == conceded)
                    {
                        form.add(1);
                    }
                    // else it's a defeat.
                    else
                    {
                        form.add(0);
                    }
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        return form;
    }

    public static void updateOppositionName(final Database database, final String oldName,
            final String newName)
    {
        final ContentValues values = new ContentValues();
        values.put(Fixtures.COL_NAME_OPPOSITION, newName);

        database.getWritableDatabase().update(Fixtures.TABLE_NAME, values,
                Fixtures.COL_NAME_OPPOSITION + "=?", new String[]{oldName});
    }

    public static void updateCompetitionName(final Database database, final String oldName,
            final String newName)
    {
        final ContentValues values = new ContentValues();
        values.put(Fixtures.COL_NAME_COMPETITION, newName);

        database.getWritableDatabase().update(Fixtures.TABLE_NAME, values,
                Fixtures.COL_NAME_COMPETITION + "=?", new String[]{oldName});
    }

    public static int[] getPointsProgress(final Database database)
    {
        Log.d(TAG, "getPointsProgress");

        int[] pointsProgress = null;
        final long nowInSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        final Cursor results = database.getReadableDatabase().query(Fixtures.TABLE_NAME,
                GOALS_FOR_CONCEDED, RESULTS_WHERE, new String[]{String.valueOf(nowInSeconds)}, null,
                null, Fixtures.DEFAULT_SORT_ORDER);
        if (results != null)
        {
            if (results.moveToFirst())
            {
                final int numResults = results.getCount();
                Log.v(TAG, "number of results: " + numResults);
                pointsProgress = new int[numResults];

                int gameIndex = 0;
                int pointsTotal = 0;
                do
                {
                    if (results.getInt(0) > results.getInt(1))
                    {
                        Log.v(TAG, "win");
                        pointsProgress[gameIndex] = pointsTotal + 3;
                    }
                    else if (results.getInt(0) == results.getInt(1))
                    {
                        Log.v(TAG, "draw");
                        pointsProgress[gameIndex] = pointsTotal + 1;
                    }
                    else
                    {
                        Log.v(TAG, "lost");
                        pointsProgress[gameIndex] = pointsTotal;
                    }

                    pointsTotal = pointsProgress[gameIndex++];

                } while (results.moveToNext());
            }

            results.close();
        }

        Log.d(TAG, "points progress: " + Arrays.toString(pointsProgress));

        return pointsProgress;
    }

    public static float[] getPointsAverage(final Database database)
    {
        Log.d(TAG, "getPointsAverage");

        float[] averagePoints = null;
        final long nowInSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        final Cursor results = database.getReadableDatabase().query(Fixtures.TABLE_NAME,
                GOALS_FOR_CONCEDED, RESULTS_WHERE, new String[]{String.valueOf(nowInSeconds)}, null,
                null, Fixtures.DEFAULT_SORT_ORDER);
        if (results != null)
        {
            if (results.moveToFirst())
            {
                final int numResults = results.getCount();
                Log.v(TAG, "number of results: " + numResults);
                averagePoints = new float[numResults];

                int gameIndex = 0;
                int pointsTotal = 0;
                do
                {
                    if (results.getInt(0) > results.getInt(1))
                    {
                        Log.v(TAG, "win");
                        pointsTotal += 3;
                    }
                    else if (results.getInt(0) == results.getInt(1))
                    {
                        Log.v(TAG, "draw");
                        pointsTotal += 1;
                    }
                    else
                    {
                        Log.v(TAG, "lost");
                    }

                    averagePoints[gameIndex++] = (float)pointsTotal / (float)gameIndex;

                } while (results.moveToNext());
            }

            results.close();
        }

        Log.d(TAG, "average points: " + Arrays.toString(averagePoints));

        return averagePoints;
    }

    public static int[] getGoalsScored(final Database database)
    {
        Log.d(TAG, "getGoalsScored");

        int[] goalsScored = null;
        final long nowInSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        final Cursor results = database.getReadableDatabase().query(Fixtures.TABLE_NAME,
                new String[]{Fixtures.COL_NAME_GOALS_SCORED}, RESULTS_WHERE,
                new String[]{String.valueOf(nowInSeconds)}, null, null,
                Fixtures.DEFAULT_SORT_ORDER);
        if (results != null)
        {
            if (results.moveToFirst())
            {
                final int numResults = results.getCount();
                Log.v(TAG, "number of results: " + numResults);
                goalsScored = new int[numResults];

                int gameIndex = 0;
                int goalsTotal = 0;
                do
                {
                    goalsTotal += results.getInt(0);
                    goalsScored[gameIndex++] = goalsTotal;

                } while (results.moveToNext());
            }

            results.close();
        }

        Log.d(TAG, "goals scored: " + Arrays.toString(goalsScored));

        return goalsScored;
    }

    public static float[] getAverageGoalsScored(final Database database)
    {
        Log.d(TAG, "getAverageGoalsScored");

        float[] averageGoalsScored = null;
        final long nowInSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        final Cursor results = database.getReadableDatabase().query(Fixtures.TABLE_NAME,
                new String[]{Fixtures.COL_NAME_GOALS_SCORED}, RESULTS_WHERE,
                new String[]{String.valueOf(nowInSeconds)}, null, null,
                Fixtures.DEFAULT_SORT_ORDER);
        if (results != null)
        {
            if (results.moveToFirst())
            {
                final int numResults = results.getCount();
                Log.v(TAG, "number of results: " + numResults);
                averageGoalsScored = new float[numResults];

                int gameIndex = 0;
                int goalsTotal = 0;
                do
                {
                    goalsTotal += results.getInt(0);
                    averageGoalsScored[gameIndex++] = (float)goalsTotal / (float)gameIndex;

                } while (results.moveToNext());
            }

            results.close();
        }

        Log.d(TAG, "average goals scored: " + Arrays.toString(averageGoalsScored));

        return averageGoalsScored;
    }

    public static int[] getGoalsConceded(final Database database)
    {
        Log.d(TAG, "getGoalsConceded");

        int[] goalsConceded = null;
        final long nowInSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        final Cursor results = database.getReadableDatabase().query(Fixtures.TABLE_NAME,
                new String[]{Fixtures.COL_NAME_GOALS_CONCEDED}, RESULTS_WHERE,
                new String[]{String.valueOf(nowInSeconds)}, null, null,
                Fixtures.DEFAULT_SORT_ORDER);
        if (results != null)
        {
            if (results.moveToFirst())
            {
                final int numResults = results.getCount();
                Log.v(TAG, "number of results: " + numResults);
                goalsConceded = new int[numResults];

                int gameIndex = 0;
                int goalsTotal = 0;
                do
                {
                    goalsTotal += results.getInt(0);
                    goalsConceded[gameIndex++] = goalsTotal;

                } while (results.moveToNext());
            }

            results.close();
        }

        Log.d(TAG, "goals conceded: " + Arrays.toString(goalsConceded));

        return goalsConceded;
    }

    public static float[] getAverageGoalsConceded(final Database database)
    {
        Log.d(TAG, "getAverageGoalsConceded");

        float[] averageGoalsConceded = null;
        final long nowInSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        final Cursor results = database.getReadableDatabase().query(Fixtures.TABLE_NAME,
                new String[]{Fixtures.COL_NAME_GOALS_CONCEDED}, RESULTS_WHERE,
                new String[]{String.valueOf(nowInSeconds)}, null, null,
                Fixtures.DEFAULT_SORT_ORDER);
        if (results != null)
        {
            if (results.moveToFirst())
            {
                final int numResults = results.getCount();
                Log.v(TAG, "number of results: " + numResults);
                averageGoalsConceded = new float[numResults];

                int gameIndex = 0;
                int goalsTotal = 0;
                do
                {
                    goalsTotal += results.getInt(0);
                    averageGoalsConceded[gameIndex++] = (float)goalsTotal / (float)gameIndex;

                } while (results.moveToNext());
            }

            results.close();
        }

        Log.d(TAG, "average goals conceded: " + Arrays.toString(averageGoalsConceded));

        return averageGoalsConceded;
    }

    public static int[] getGoalDifference(final Database database)
    {
        Log.d(TAG, "getGoalDifference");

        int[] goalDifference = null;
        final long nowInSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        final Cursor results = database.getReadableDatabase().query(Fixtures.TABLE_NAME,
                GOALS_FOR_CONCEDED, RESULTS_WHERE, new String[]{String.valueOf(nowInSeconds)}, null,
                null, Fixtures.DEFAULT_SORT_ORDER);
        if (results != null)
        {
            if (results.moveToFirst())
            {
                final int numResults = results.getCount();
                Log.v(TAG, "number of results: " + numResults);
                goalDifference = new int[numResults];

                int gameIndex = 0;
                int totalGoalsFor = 0;
                int totalGoalsAgainst = 0;
                do
                {
                    totalGoalsFor += results.getInt(0);
                    totalGoalsAgainst += results.getInt(1);
                    goalDifference[gameIndex++] = totalGoalsFor - totalGoalsAgainst;
                } while (results.moveToNext());
            }

            results.close();
        }

        Log.d(TAG, "goal difference: " + Arrays.toString(goalDifference));

        return goalDifference;
    }

    public static float[] getAverageGoalDifference(final Database database)
    {
        Log.d(TAG, "getAverageGoalDifference");

        float[] avgGoalDiff = null;
        final long nowInSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        final Cursor results = database.getReadableDatabase().query(Fixtures.TABLE_NAME,
                GOALS_FOR_CONCEDED, RESULTS_WHERE, new String[]{String.valueOf(nowInSeconds)}, null,
                null, Fixtures.DEFAULT_SORT_ORDER);
        if (results != null)
        {
            if (results.moveToFirst())
            {
                final int numResults = results.getCount();
                Log.v(TAG, "number of results: " + numResults);
                avgGoalDiff = new float[numResults];

                float gameIndex = 0;
                float totalGoalsFor = 0;
                float totalGoalsAgainst = 0;
                do
                {
                    totalGoalsFor += results.getInt(0);
                    totalGoalsAgainst += results.getInt(1);
                    avgGoalDiff[(int)gameIndex++] = (totalGoalsFor - totalGoalsAgainst) / gameIndex;
                } while (results.moveToNext());
            }

            results.close();
        }

        Log.d(TAG, "average goal difference: " + Arrays.toString(avgGoalDiff));

        return avgGoalDiff;
    }

    public static int[] getNumberOfWinsDrawsLosses(final Database database)
    {
        int[] countOfWinsDrawsLosses = new int[]{0, 0, 0}; // wins, draws, losses
        final long nowInSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        final Cursor results = database.getReadableDatabase().query(Fixtures.TABLE_NAME,
                GOALS_FOR_CONCEDED, RESULTS_WHERE, new String[]{String.valueOf(nowInSeconds)}, null,
                null, Fixtures.DEFAULT_SORT_ORDER);
        if (results != null)
        {
            if (results.moveToFirst())
            {
                do
                {
                    if (results.getInt(0) > results.getInt(1))
                    {
                        countOfWinsDrawsLosses[0]++;
                    }
                    else if (results.getInt(0) == results.getInt(1))
                    {
                        countOfWinsDrawsLosses[1]++;
                    }
                    else
                    {
                        countOfWinsDrawsLosses[2]++;
                    }
                } while (results.moveToNext());
            }

            results.close();
        }

        return countOfWinsDrawsLosses;
    }

    public static int[] getAttendances(final Database database, final Context context)
    {
        Log.v(TAG, "getAttendances");
        int[] attendances = null;
        final long nowInSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        final Cursor results = database.getReadableDatabase().query(Fixtures.TABLE_NAME,
                new String[]{Fixtures.COL_NAME_ATTENDANCE},
                Fixtures.COL_NAME_DATE + "<? AND " + Fixtures.COL_NAME_VENUE + "=? AND " +
                        Fixtures.COL_NAME_ATTENDANCE + ">0",
                new String[]{String.valueOf(nowInSeconds), context.getString(R.string.home)}, null,
                null, Fixtures.DEFAULT_SORT_ORDER);
        if (results != null)
        {
            if (results.moveToFirst())
            {
                final int numResults = results.getCount();
                Log.v(TAG, "number of results: " + numResults);
                attendances = new int[numResults];
                int i = 0;
                do
                {
                    attendances[i++] = results.getInt(0);
                } while (results.moveToNext());
            }

            results.close();
        }

        return attendances;
    }
}
