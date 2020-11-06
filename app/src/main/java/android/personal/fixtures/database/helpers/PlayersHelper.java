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

    private static final String CURRENT_PLAYER_SELECTION = Players.COL_NAME_CURRENT + "=1";

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

    /**
     * Get all player names along with their IDs.
     *
     * @param database   The database object to get the data from.
     * @param getCurrent <code>True</code> if we only want the players currently at the club,
     *                   <code>false</code> otherwise.
     * @return Cursor of all relevant player names with IDs.
     */
    public static Cursor getAllNamesWithIds(final Database database, final boolean getCurrent)
    {
        final String selection = getCurrent ? CURRENT_PLAYER_SELECTION : null;
        final Cursor names = database.getReadableDatabase().query(Players.TABLE_NAME,
                new String[]{BaseColumns._ID, Players.COL_NAME_FORENAME, Players.COL_NAME_SURNAME},
                selection, null, null, null, Players.NAME_SORT_ORDER);

        if (names != null)
        {
            names.moveToFirst();
        }

        return names;
    }

    /**
     * Get all the players currently at the club.
     */
    public static Cursor getCurrentPlayers(final Database database)
    {
        final Cursor players = database.getReadableDatabase().query(Players.TABLE_NAME, null,
                CURRENT_PLAYER_SELECTION, null, null, null, Players.NAME_SORT_ORDER);

        if (players != null)
        {
            players.moveToFirst();
        }

        return players;
    }
}
