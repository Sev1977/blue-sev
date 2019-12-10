package android.personal.fixtures.database.helpers;

import android.database.Cursor;
import android.personal.fixtures.database.Database;
import android.personal.fixtures.database.tables.Clubs;
import android.provider.BaseColumns;
import android.util.Log;

/**
 *
 */
public class ClubsHelper
{
    private static final String TAG = ClubsHelper.class.getSimpleName();

    public static Cursor getAllLeagueClubs(final Database database)
    {
        Log.d(TAG, "getAllLeagueClubs");
        final Cursor allLeagueClubs = database.getSelection(Clubs.TABLE_NAME,
                Clubs.COL_NAME_IS_LEAGUE + "=1", null, Clubs.DEFAULT_SORT_ORDER);
        if (allLeagueClubs != null)
        {
            allLeagueClubs.moveToFirst();
        }
        return allLeagueClubs;
    }

    public static boolean removeClubWithId(final Database database, final long id)
    {
        final int numRowsDeleted = database.getWritableDatabase().delete(Clubs.TABLE_NAME,
                BaseColumns._ID + "=?", new String[]{String.valueOf(id)});
        return numRowsDeleted > 0;
    }

    public static boolean removeClubWithName(final Database database, final String fullName)
    {
        final int numRowsDeleted = database.getWritableDatabase().delete(Clubs.TABLE_NAME,
                Clubs.COL_NAME_FULL_NAME + "=?", new String[]{fullName});
        return numRowsDeleted > 0;
    }

    public static String getFullNameFromId(final Database database, final long clubId)
    {
        return getName(database, Clubs.COL_NAME_FULL_NAME, clubId);
    }

    public static String getFullNameFromShortName(final Database database, final String shortName)
    {
        String fullName = null;

        final Cursor club = database.getReadableDatabase().query(Clubs.TABLE_NAME,
                new String[]{Clubs.COL_NAME_FULL_NAME}, Clubs.COL_NAME_SHORT_NAME + "=?",
                new String[]{shortName}, null, null, null);

        if (club != null)
        {
            if (club.moveToFirst())
            {
                fullName = club.getString(0);
            }
            club.close();
        }

        return fullName;
    }

    public static String getShortNameFromId(final Database database, final long clubId)
    {
        return getName(database, Clubs.COL_NAME_SHORT_NAME, clubId);
    }

    private static String getName(final Database database, final String nameColumnName,
            final long clubId)
    {
        String fullName = null;

        final Cursor club = database.getReadableDatabase().query(Clubs.TABLE_NAME,
                new String[]{nameColumnName}, BaseColumns._ID + "=?",
                new String[]{String.valueOf(clubId)}, null, null, null);

        if (club != null)
        {
            if (club.moveToFirst())
            {
                fullName = club.getString(0);
            }
            club.close();
        }

        return fullName;
    }

    public static Cursor getAllCodes(final Database database)
    {
        final Cursor codes = database.getReadableDatabase().query(Clubs.TABLE_NAME,
                new String[]{Clubs.COL_NAME_CODE}, null, null, null, null,
                Clubs.DEFAULT_SORT_ORDER);
        if (codes != null)
        {
            codes.moveToFirst();
        }
        return codes;
    }
}
