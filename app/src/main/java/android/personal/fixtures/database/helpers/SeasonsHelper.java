package android.personal.fixtures.database.helpers;

import android.database.Cursor;
import android.personal.fixtures.database.Database;
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
}
