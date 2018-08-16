package android.personal.fixtures.database;

import android.database.Cursor;
import android.personal.fixtures.database.tables.Fixtures;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class FixturesHelper
{
    private static final String TAG = FixturesHelper.class.getSimpleName();

    public static Cursor getAllResults(final Database database)
    {
        Log.d(TAG, "getAllResults");
        final long now = System.currentTimeMillis();
        final long nowInSeconds = TimeUnit.MILLISECONDS.toSeconds(now);
        final Cursor allFixtures = database.getSelection(Fixtures.TABLE_NAME,
                Fixtures.COL_NAME_DATE + "<?", new String[]{String.valueOf(nowInSeconds)},
                Fixtures.DEFAULT_SORT_ORDER);
        if (allFixtures != null)
        {
            allFixtures.moveToFirst();
        }
        return allFixtures;
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

    public static boolean removeFixtureWithId(final Database database, final long id)
    {
        final int numRowsDeleted = database.getWritableDatabase().delete(Fixtures.TABLE_NAME,
                BaseColumns._ID + "=?", new String[]{String.valueOf(id)});
        return numRowsDeleted > 0;
    }

    public static ArrayList<Integer> getRecentForm(final Database database)
    {
        final ArrayList<Integer> form = new ArrayList<>();

        final long now = System.currentTimeMillis();
        final long nowInSeconds = TimeUnit.MILLISECONDS.toSeconds(now);
        final Cursor cursor = database.getReadableDatabase().query(Fixtures.TABLE_NAME,
                new String[]{Fixtures.COL_NAME_GOALS_SCORED, Fixtures.COL_NAME_GOALS_CONCEDED},
                Fixtures.COL_NAME_DATE + "<?", new String[]{String.valueOf(nowInSeconds)}, null,
                null, Fixtures.COL_NAME_DATE + " " + "DESC " + "LIMIT 6");
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
                        form.add(2);
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
}
