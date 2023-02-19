package android.personal.fixtures;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 *
 */
public class Settings
{
    /**
     * @param appContext
     * @return
     */
    public static int getSelectedSeasonId(final Context appContext)
    {
        return PreferenceManager.getDefaultSharedPreferences(appContext).getInt(
                appContext.getString(R.string.pref_key_season), 0);
    }

    /**
     * @param appContext
     * @return
     */
    public static boolean showLeagueOnly(final Context appContext)
    {
        return PreferenceManager.getDefaultSharedPreferences(appContext).getBoolean(
                appContext.getString(R.string.pref_key_league_only), false);
    }

    /**
     *
     * @param appContext
     * @return
     */
    public static boolean showCurrentPlayersOnly(final Context appContext)
    {
        return PreferenceManager.getDefaultSharedPreferences(appContext).getBoolean(
                appContext.getString(R.string.pref_key_show_current_players), false);
    }

    public static void setShowCurrentPlayersOnly(final Context appContext, final boolean set)
    {
        PreferenceManager.getDefaultSharedPreferences(appContext).edit()
                .putBoolean(appContext.getString(R.string.pref_key_show_current_players), set)
                .apply();
    }

    /**
     *
     * @param appContext
     * @return
     */
    public static boolean showCurrentOpponentsOnly(final Context appContext)
    {
        return PreferenceManager.getDefaultSharedPreferences(appContext).getBoolean(
                appContext.getString(R.string.pref_key_show_current_opponents), false);
    }
}
