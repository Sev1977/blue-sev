package android.personal.fixtures.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.personal.fixtures.R;
import android.personal.fixtures.database.tables.Fixtures;
import android.personal.fixtures.database.tables.Statistics;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * <ol>
 * <li>points</li>
 * <li>number of wins</li>
 * <li>number of draws</li>
 * <li>number of defeats</li>
 * <li>goals scored</li>
 * <li>goals conceded</li>
 * <li>goals difference</li>
 * <li>average attendance</li>
 * <li>minimum attendance</li>
 * <li>maximum attendance</li>
 * <li>top scorer</li>
 * <li>win percentage</li>
 * <li>current unbeaten run</li>
 * <li>current winning run</li>
 * <li>current losing run</li>
 * <li>longest unbeaten run</li>
 * <li>longest winning run</li>
 * <li>longest losing run</li>
 * <li>number of games since failing to score</li>
 * <li>number of games since conceding</li>
 * <li>number of games when failed to score</li>
 * <li>number of clean sheets</li>
 * <li>number of nil-nil draws</li>
 * </ol>
 */
public class StatsUpdate implements Runnable
{
    private static final String TAG = StatsUpdate.class.getSimpleName();

    private static final int ALL = 0;
    private static final int ALL_HOME = 1;
    private static final int ALL_AWAY = 2;
    private static final int LAST_SIX = 3;
    private static final int LAST_SIX_HOME = 4;
    private static final int LAST_SIX_AWAY = 5;
    private static final int LGE_ALL = 6;
    private static final int LGE_ALL_HOME = 7;
    private static final int LGE_ALL_AWAY = 8;
    private static final int LGE_LAST_SIX = 9;
    private static final int LGE_LAST_SIX_HOME = 10;
    private static final int LGE_LAST_SIX_AWAY = 11;
    private static final int NUM_FILTERS = 12;

    private static final int POINTS = 0;
    private static final int NUMBER_OF_WINS = 1;
    private static final int NUMBER_OF_DRAWS = 2;
    private static final int NUMBER_OF_DEFEATS = 3;
    private static final int GOALS_SCORED = 4;
    private static final int GOALS_CONCEDED = 5;
    private static final int GOALS_DIFFERENCE = 6;
    private static final int AVERAGE_ATTENDANCE = 7;
    private static final int MINIMUM_ATTENDANCE = 8;
    private static final int MAXIMUM_ATTENDANCE = 9;
    private static final int TOP_SCORER = 10;
    private static final int WIN_PERCENTAGE = 11;
    private static final int CURRENT_UNBEATEN_RUN = 12;
    private static final int CURRENT_WINNING_RUN = 13;
    private static final int CURRENT_LOSING_RUN = 14;
    private static final int LONGEST_UNBEATEN_RUN = 15;
    private static final int LONGEST_WINNING_RUN = 16;
    private static final int LONGEST_LOSING_RUN = 17;
    private static final int NUMBER_OF_GAMES_SINCE_FAILING_TO_SCORE = 18;
    private static final int NUMBER_OF_GAMES_SINCE_CONCEDING = 19;
    private static final int NUMBER_OF_GAMES_WHEN_FAILED_TO_SCORE = 20;
    private static final int NUMBER_OF_CLEAN_SHEETS = 21;
    private static final int NUMBER_OF_NIL_NIL_DRAWS = 22;
    private static final int NUM_STATISTICS = 23;

    private Context mContext;
    private long mSeasonId;
    private Database mDatabase;

    public StatsUpdate(final Context context, final long seasonId)
    {
        Log.d(TAG, "Constructor");
        mContext = context;
        mSeasonId = seasonId;
    }

    @Override
    public void run()
    {
        Log.d(TAG, "run");

        mDatabase = Database.getInstance(mContext);
        final Cursor stats = mDatabase.getSelection(Statistics.TABLE_NAME,
                Statistics.COL_NAME_SEASON_ID + "=?", new String[]{String.valueOf(mSeasonId)},
                Statistics.COL_NAME_DESCRIPTION + " ASC");
        if ((stats == null) || (stats.getCount() == 0))
        {
            insertNewRecords();
        }

        // Update the existing stats
        updateStats();
    }

    private void insertNewRecords()
    {
        Log.d(TAG, "insert the new stats for this season");

        final String[] descriptions = mContext.getResources().getStringArray(
                R.array.numerical_stats_descriptions);
        final String[] gameTypes = mContext.getResources().getStringArray(
                R.array.game_type_filters);
        final String[] filters = mContext.getResources().getStringArray(R.array.numbers_filters);
        final SQLiteDatabase sqLiteDatabase = mDatabase.getWritableDatabase();
        // We need to add the stats from scratch
        final ContentValues values = new ContentValues();
        // For each filter and game type we must insert a new list of records for the descriptions
        for (final String gameType : gameTypes)
        {
            for (final String filter : filters)
            {
                for (final String description : descriptions)
                {
                    values.clear();
                    values.put(Statistics.COL_NAME_SEASON_ID, mSeasonId);
                    values.put(Statistics.COL_NAME_GAME_TYPE, gameType);
                    values.put(Statistics.COL_NAME_FILTER, filter);
                    values.put(Statistics.COL_NAME_DESCRIPTION, description);
                    sqLiteDatabase.insert(Statistics.TABLE_NAME, null, values);
                }
            }
        }
    }

    private void updateStats()
    {
        Log.d(TAG, "updateStats");

        /* Get all results to date */
        final Calendar now = Calendar.getInstance(Locale.UK);
        final Cursor results = mDatabase.getSelection(Fixtures.TABLE_NAME,
                Fixtures.COL_NAME_DATE + "<?", new String[]{
                        String.valueOf(TimeUnit.MILLISECONDS.toSeconds(now.getTimeInMillis()))},
                Fixtures.DEFAULT_SORT_ORDER);
        if (results != null)
        {
            if (results.getCount() > 0)
            {
                Log.d(TAG, "number of results = " + results.getCount());

                final ArrayList<Integer[]> values = new ArrayList<>();
                clearValues(values);

                final Integer[] allGames = values.get(ALL);
                final Integer[] allLeagueGames = values.get(LGE_ALL);

                /* Traverse the results, filling in the statistic values as we inspect each one */
                int numberOfGames = 0;
                int numberOfHomeGames = 0;
                int numberOfAwayGames = 0;
                int numberOfLeagueGames = 0;
                int numberOfLeagueHomeGames = 0;
                int numberOfLeagueAwayGames = 0;
                results.moveToFirst();
                do
                {
                    if (results.getInt(Fixtures.COL_ID_SEASON_ID) == mSeasonId)
                    {
                        final String compName = results.getString(Fixtures.COL_ID_COMPETITION);
                        final boolean isLeagueGame = CompetitionsHelper.getIsLeague(mDatabase,
                                compName);
                        final int goalsFor = results.getInt(Fixtures.COL_ID_GOALS_SCORED);
                        final int goalsAgainst = results.getInt(Fixtures.COL_ID_GOALS_CONCEDED);
                        final boolean isHome = "home".equalsIgnoreCase(
                                results.getString(Fixtures.COL_ID_VENUE));

                        final int allHomeAwayIndex = isHome ? ALL_HOME : ALL_AWAY;
                        final int allLgeHomeAwayIndex = isHome ? LGE_ALL_HOME : LGE_ALL_AWAY;

                        numberOfGames++;
                        if (isHome)
                        {
                            numberOfHomeGames++;
                        }
                        else
                        {
                            numberOfAwayGames++;
                        }
                        if (isLeagueGame)
                        {
                            numberOfLeagueGames++;
                            if (isHome)
                            {
                                numberOfLeagueHomeGames++;
                            }
                            else
                            {
                                numberOfLeagueAwayGames++;
                            }
                        }

                        /* Points/Current winning streak/longest winning streak/current
                         * unbeaten streak/current unbeaten streak/current losing streak/longest
                         * losing streak */
                        if (goalsFor > goalsAgainst)
                        {
                            // Win
                            Log.v(TAG, "Win += 3");
                            updateWinningStreaks(allGames);
                            updateWinningStreaks(values.get(allHomeAwayIndex));
                            // Update the win percentages
                            allGames[WIN_PERCENTAGE] = allGames[NUMBER_OF_WINS] / numberOfGames;
                            values.get(ALL_HOME)[WIN_PERCENTAGE] = values.get(
                                    ALL_HOME)[NUMBER_OF_WINS] / numberOfHomeGames;
                            values.get(ALL_AWAY)[WIN_PERCENTAGE] = values.get(
                                    ALL_AWAY)[NUMBER_OF_WINS] / numberOfAwayGames;

                            if (isLeagueGame)
                            {
                                final Integer[] allLeagueHoAGames = values.get(allLgeHomeAwayIndex);
                                allLeagueGames[POINTS] += 3;
                                allLeagueHoAGames[POINTS] += 3;
                                updateWinningStreaks(allLeagueGames);
                                updateWinningStreaks(allLeagueHoAGames);
                                // Update the win percentages
                                allLeagueGames[WIN_PERCENTAGE] =
                                        allLeagueGames[NUMBER_OF_WINS] / numberOfLeagueGames;
                                values.get(LGE_ALL_HOME)[WIN_PERCENTAGE] = values.get(
                                        LGE_ALL_HOME)[NUMBER_OF_WINS] / numberOfLeagueHomeGames;
                                values.get(LGE_ALL_AWAY)[WIN_PERCENTAGE] = values.get(
                                        LGE_ALL_AWAY)[NUMBER_OF_WINS] / numberOfLeagueAwayGames;
                            }
                        }
                        else if (goalsFor == goalsAgainst)
                        {
                            // draw
                            Log.v(TAG, "Draw += 1");
                            updateDrawingStreaks(allGames);
                            updateDrawingStreaks(values.get(allHomeAwayIndex));
                            if (isLeagueGame)
                            {
                                final Integer[] allLeagueHoAGames = values.get(allLgeHomeAwayIndex);
                                allLeagueGames[POINTS] += 1;
                                allLeagueHoAGames[POINTS] += 1;
                                updateDrawingStreaks(allLeagueGames);
                                updateDrawingStreaks(allLeagueHoAGames);
                            }
                        }
                        else
                        {
                            Log.v(TAG, "Defeat += 0");
                            updateLosingStreaks(allGames);
                            updateLosingStreaks(values.get(allHomeAwayIndex));
                            if (isLeagueGame)
                            {
                                updateLosingStreaks(allLeagueGames);
                                updateLosingStreaks(values.get(allLgeHomeAwayIndex));
                            }
                        }

                        /* goals for */
                        updateGoalsFor(allGames, goalsFor);
                        updateGoalsFor(values.get(allHomeAwayIndex), goalsFor);
                        if (isLeagueGame)
                        {
                            updateGoalsFor(allLeagueGames, goalsFor);
                            updateGoalsFor(values.get(allLgeHomeAwayIndex), goalsFor);
                        }

                        /* goals against */
                        updateGoalsAgainst(allGames, goalsAgainst);
                        updateGoalsAgainst(values.get(allHomeAwayIndex), goalsAgainst);
                        if (isLeagueGame)
                        {
                            updateGoalsAgainst(allLeagueGames, goalsAgainst);
                            updateGoalsAgainst(values.get(allLgeHomeAwayIndex), goalsAgainst);
                        }

                        /* goals difference */
                        final int goalDifference = goalsFor - goalsAgainst;
                        allGames[GOALS_DIFFERENCE] += goalDifference;
                        values.get(allHomeAwayIndex)[GOALS_DIFFERENCE] += goalDifference;
                        if (isLeagueGame)
                        {
                            allLeagueGames[GOALS_DIFFERENCE] += goalDifference;
                            values.get(allLgeHomeAwayIndex)[GOALS_DIFFERENCE] += goalDifference;
                        }

                        /* total goalless, 0-0 draws */
                        if ((goalsFor == 0) && (goalsAgainst == 0))
                        {
                            allGames[NUMBER_OF_NIL_NIL_DRAWS]++;
                            values.get(allHomeAwayIndex)[NUMBER_OF_NIL_NIL_DRAWS]++;
                            if (isLeagueGame)
                            {
                                allLeagueGames[NUMBER_OF_NIL_NIL_DRAWS]++;
                                values.get(allLgeHomeAwayIndex)[NUMBER_OF_NIL_NIL_DRAWS]++;
                            }
                        }
                    }
                } while (results.moveToNext());

                updateDb(values);
            }

            results.close();
        }
    }

    private void updateDb(final ArrayList<Integer[]> values)
    {
        Log.d(TAG, "updateDb");

        final String[] descriptions = mContext.getResources().getStringArray(
                R.array.numerical_stats_descriptions);
        final String[] gameTypes = mContext.getResources().getStringArray(
                R.array.game_type_filters);
        final String[] filters = mContext.getResources().getStringArray(R.array.numbers_filters);
        final SQLiteDatabase sqLiteDatabase = mDatabase.getWritableDatabase();
        // We need to add the stats from scratch
        final ContentValues contentValues = new ContentValues();

        // All or league
        for (int gameTypeIndex = 0; gameTypeIndex < gameTypes.length; gameTypeIndex++)
        {
            // All, last 6 home, etc.
            for (int filterIndex = 0; filterIndex < filters.length; filterIndex++)
            {
                // Points, winning streak, etc.
                for (int descriptionIndex = 0; descriptionIndex < descriptions.length;
                        descriptionIndex++)
                {
                    contentValues.clear();
                    contentValues.put(Statistics.COL_NAME_SEASON_ID, mSeasonId);
                    contentValues.put(Statistics.COL_NAME_GAME_TYPE, gameTypes[gameTypeIndex]);
                    contentValues.put(Statistics.COL_NAME_FILTER, filters[filterIndex]);
                    contentValues.put(Statistics.COL_NAME_DESCRIPTION,
                            descriptions[descriptionIndex]);
                    final int x = filterIndex + (filters.length * gameTypeIndex);
                    contentValues.put(Statistics.COL_NAME_VALUE, values.get(x)[descriptionIndex]);
                    final long updatedRowId = sqLiteDatabase.insert(Statistics.TABLE_NAME, null,
                            contentValues);
                    if (updatedRowId == -1)
                    {
                        Log.e(TAG,
                                "Error updating statistic, [" + gameTypeIndex + "][" + filterIndex +
                                        "][" + descriptionIndex + "]");
                    }
                }
            }
        }
    }

    /**
     * Clear the array of arrays
     */
    private static void clearValues(final ArrayList<Integer[]> arrays)
    {
        arrays.clear();

        for (int x = 0; x < NUM_FILTERS; x++)
        {
            final Integer[] array = new Integer[NUM_STATISTICS];
            Arrays.fill(array, 0);
            arrays.add(array);
        }
    }

    private static void updateWinningStreaks(final Integer[] array)
    {
        array[NUMBER_OF_WINS]++;
        array[CURRENT_WINNING_RUN]++;
        if (array[CURRENT_WINNING_RUN] > array[LONGEST_WINNING_RUN])
        {
            array[LONGEST_WINNING_RUN] = array[CURRENT_WINNING_RUN];
        }
        updateUnbeatenRun(array);
    }

    private static void updateDrawingStreaks(final Integer[] array)
    {
        array[NUMBER_OF_DRAWS]++;
        updateUnbeatenRun(array);
        array[CURRENT_WINNING_RUN] = 0;
    }

    private static void updateUnbeatenRun(final Integer[] array)
    {
        array[CURRENT_UNBEATEN_RUN]++;
        if (array[CURRENT_UNBEATEN_RUN] > array[LONGEST_UNBEATEN_RUN])
        {
            array[LONGEST_UNBEATEN_RUN] = array[CURRENT_UNBEATEN_RUN];
        }
        array[CURRENT_LOSING_RUN] = 0;
    }

    private static void updateLosingStreaks(final Integer[] array)
    {
        array[NUMBER_OF_DEFEATS]++;
        array[CURRENT_LOSING_RUN]++;
        if (array[CURRENT_LOSING_RUN] > array[LONGEST_LOSING_RUN])
        {
            array[LONGEST_LOSING_RUN] = array[CURRENT_LOSING_RUN];
        }
        array[CURRENT_WINNING_RUN] = 0;
        array[CURRENT_UNBEATEN_RUN] = 0;
    }

    private static void updateGoalsFor(final Integer[] array, final int goalsFor)
    {
        array[GOALS_SCORED] += goalsFor;
        if (goalsFor > 0)
        {
            array[NUMBER_OF_GAMES_SINCE_FAILING_TO_SCORE] = 0;
        }
        else
        {
            array[NUMBER_OF_GAMES_SINCE_FAILING_TO_SCORE]++;
            array[NUMBER_OF_GAMES_WHEN_FAILED_TO_SCORE]++;
        }
    }

    private static void updateGoalsAgainst(final Integer[] array, final int goalsAgainst)
    {
        array[GOALS_CONCEDED] += goalsAgainst;
        if (goalsAgainst > 0)
        {
            array[NUMBER_OF_GAMES_SINCE_CONCEDING] = 0;
        }
        else
        {
            array[NUMBER_OF_GAMES_SINCE_CONCEDING]++;
            array[NUMBER_OF_CLEAN_SHEETS]++;
        }
    }
}
