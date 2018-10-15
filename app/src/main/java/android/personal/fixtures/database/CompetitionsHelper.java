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

    /**
     * Find out if the given competition is a league.
     *
     * @param database  The database to search
     * @param shortName The short name of the competition to search for
     * @return <code>true</code> if the given competition is a league, <code>false</code> otherwise.
     */
    public static boolean getIsLeague(final Database database, final String shortName)
    {
        boolean isLeague = false;
        final Cursor competition = (shortName == null) ? null : database.getColumnsForSelection(
                Competitions.TABLE_NAME, new String[]{Competitions.COL_NAME_IS_LEAGUE},
                Competitions.COL_NAME_SHORT_NAME + "=?", new String[]{shortName},
                Competitions.DEFAULT_SORT_ORDER);
        if (competition != null)
        {
            if (competition.getCount() == 1)
            {
                isLeague = (competition.getInt(0) == 1);
            }
            competition.close();
        }

        return isLeague;
    }

    /**
     * Get the data for [the] league competition(s)
     *
     * @param database the database to search
     * @return A Cursor containing all the records for competitions listed as a league.
     */
    public static Cursor getLeague(final Database database)
    {
        return database.getColumnsForSelection(Competitions.TABLE_NAME, null,
                Competitions.COL_NAME_IS_LEAGUE + "=1", null, null);
    }
}
