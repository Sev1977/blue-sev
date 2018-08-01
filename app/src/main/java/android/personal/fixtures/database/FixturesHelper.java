package android.personal.fixtures.database;

import android.database.Cursor;
import android.personal.fixtures.database.tables.Fixtures;
import android.provider.BaseColumns;
import android.util.Log;

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
}
