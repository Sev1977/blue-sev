package android.personal.fixtures.database.helpers;

import android.content.Context;
import android.database.Cursor;
import android.personal.fixtures.Settings;
import android.personal.fixtures.database.Database;
import android.personal.fixtures.database.tables.Competitions;
import android.personal.fixtures.database.tables.Seasons;
import android.provider.BaseColumns;

public class SeasonsHelper
{
    public static String getSeasonName(final Database database, final int id)
    {
        String name = null;
        final Cursor season = database.getColumnsForSelection(Seasons.TABLE_NAME,
                new String[]{Seasons.COL_NAME_NAME}, BaseColumns._ID + "=?",
                new String[]{String.valueOf(id)}, Seasons.DEFAULT_SORT_ORDER);
        if (season != null)
        {
            if (season.moveToFirst())
            {
                name = season.getString(0);
            }
            season.close();
        }

        return name;
    }

    /**
     * Get the short name for the currently selected season.
     *
     * @param database
     * @param appContext
     * @param getFullName
     * @return
     */
    static String getSelectedSeasonName(final Database database, final Context appContext,
            final boolean getFullName)
    {
        // Get the long name for the current season.
        final int seasonId = Settings.getSelectedSeasonId(appContext);
        String seasonName = getSeasonName(database, seasonId);
        // If we're asking for the full name, we can return it right now.
        if (getFullName)
        {
            return seasonName;
        }

        seasonName = seasonName.substring(0, seasonName.indexOf(" "));

        // Now get all the league competitions and find the one with the long name.
        final Cursor comps = CompetitionsHelper.getAllLeagues(database);
        if (comps != null)
        {
            if (comps.moveToFirst())
            {
                do
                {
                    String shortName = comps.getString(Competitions.COL_INDEX_SHORT_NAME);
                    if (shortName.contains(seasonName))
                    {
                        seasonName = shortName;
                        break;
                    }
                } while (comps.moveToNext());
            }

            comps.close();
        }

        return seasonName;
    }
}
