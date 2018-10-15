package android.personal.fixtures.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.personal.fixtures.database.tables.Competitions;
import android.personal.fixtures.database.tables.Fixtures;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class FixturesHelper
{
    private static final String TAG = FixturesHelper.class.getSimpleName();

    private static String getLeagueName(final Database database)
    {
        String name = "";
        final Cursor league = CompetitionsHelper.getLeague(database);
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

    public static Cursor getAllResults(final Database database, final String sortOrder)
    {
        Log.d(TAG, "getAllResults");
        final long now = System.currentTimeMillis();
        final long nowInSeconds = TimeUnit.MILLISECONDS.toSeconds(now);
        final Cursor allResults = database.getSelection(Fixtures.TABLE_NAME,
                Fixtures.COL_NAME_DATE + "<?", new String[]{String.valueOf(nowInSeconds)},
                (sortOrder == null) ? Fixtures.DEFAULT_SORT_ORDER : sortOrder);
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
                Fixtures.DEFAULT_SORT_ORDER);
        if (leagueResults != null)
        {
            leagueResults.moveToFirst();
        }
        return leagueResults;
    }

    public static Cursor getAllFixtures(final Database database)
    {
        Log.d(TAG, "getAllFixtures");

        final long now = System.currentTimeMillis();
        final long nowInSeconds = TimeUnit.MILLISECONDS.toSeconds(now);
        final Cursor allFixtures = database.getSelection(Fixtures.TABLE_NAME,
                Fixtures.COL_NAME_DATE + ">?", new String[]{String.valueOf(nowInSeconds)},
                Fixtures.DEFAULT_SORT_ORDER);
        if (allFixtures != null)
        {
            allFixtures.moveToFirst();
        }
        return allFixtures;
    }

    public static Cursor getLeagueFixtures(final Database database)
    {
        Log.d(TAG, "getLeagueFixtures");

        // Now we can get all the league results
        final long now = System.currentTimeMillis();
        final long nowInSeconds = TimeUnit.MILLISECONDS.toSeconds(now);
        final Cursor leagueFixtures = database.getSelection(Fixtures.TABLE_NAME,
                Fixtures.COL_NAME_DATE + ">? AND " + Fixtures.COL_NAME_COMPETITION + "=?",
                new String[]{String.valueOf(nowInSeconds), getLeagueName(database)},
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
                new String[]{Fixtures.COL_NAME_GOALS_SCORED, Fixtures.COL_NAME_GOALS_CONCEDED},
                Fixtures.COL_NAME_DATE + "<?", new String[]{String.valueOf(nowInSeconds)}, null,
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
}
