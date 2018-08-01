package android.personal.fixtures.database;

import android.database.Cursor;
import android.personal.fixtures.database.tables.Competitions;
import android.provider.BaseColumns;

/**
 *
 */
public class CompetitionsHelper
{
    private static final String TAG = CompetitionsHelper.class.getSimpleName();

    public static boolean removeCompetitionWithId(final Database database, final long id)
    {
        final int numRowsDeleted = database.getWritableDatabase().delete(Competitions.TABLE_NAME,
                BaseColumns._ID + "=?", new String[]{String.valueOf(id)});
        return numRowsDeleted > 0;
    }

    public static boolean removeCompeitionWithName(final Database database, final String fullName)
    {
        final int numRowsDeleted = database.getWritableDatabase().delete(Competitions.TABLE_NAME,
                Competitions.COL_NAME_FULL_NAME + "=?", new String[]{fullName});
        return numRowsDeleted > 0;
    }

    public static String getFullNameFromId(final Database database, final long id)
    {
        return getName(database, Competitions.COL_NAME_FULL_NAME, id);
    }

    public static String getShortNameFromId(final Database database, final long id)
    {
        return getName(database, Competitions.COL_NAME_SHORT_NAME, id);
    }

    private static String getName(final Database database, final String nameColumnName,
            final long id)
    {
        String fullName = null;

        final Cursor competition = database.getReadableDatabase().query(Competitions.TABLE_NAME,
                new String[]{nameColumnName}, BaseColumns._ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (competition != null)
        {
            if (competition.moveToFirst())
            {
                fullName = competition.getString(0);
            }
            competition.close();
        }

        return fullName;
    }
}
