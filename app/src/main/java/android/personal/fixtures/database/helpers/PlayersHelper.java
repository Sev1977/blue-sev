package android.personal.fixtures.database.helpers;

import android.database.Cursor;
import android.personal.fixtures.database.Database;
import android.personal.fixtures.database.tables.Players;
import android.provider.BaseColumns;

/**
 *
 */
public class PlayersHelper
{
    private static final String TAG = PlayersHelper.class.getSimpleName();

    public static boolean removePlayerWithId(final Database database, final long id)
    {
        final int numRowsDeleted = database.getWritableDatabase().delete(Players.TABLE_NAME,
                BaseColumns._ID + "=?", new String[]{String.valueOf(id)});
        return numRowsDeleted > 0;
    }

    public static String getFullNameFromId(final Database database, final long id)
    {
        String fullName = null;

        final Cursor player = database.getReadableDatabase().query(Players.TABLE_NAME,
                new String[]{Players.COL_NAME_FORENAME, Players.COL_NAME_SURNAME},
                BaseColumns._ID + "=?", new String[]{String.valueOf(id)}, null, null, null);

        if (player != null)
        {
            if (player.moveToFirst())
            {
                fullName = player.getString(0) + " " + player.getString(1);
            }
            player.close();
        }

        return fullName;
    }

    public static Cursor getAllNamesWithIds(final Database database)
    {
        final Cursor names = database.getReadableDatabase().query(Players.TABLE_NAME,
                new String[]{BaseColumns._ID, Players.COL_NAME_FORENAME, Players.COL_NAME_SURNAME},
                null, null, null, null, Players.NAME_SORT_ORDER);

        if (names != null)
        {
            names.moveToFirst();
        }

        return names;
    }
}
