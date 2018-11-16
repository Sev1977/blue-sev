package android.personal.fixtures;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.personal.fixtures.database.Database;
import android.personal.fixtures.database.tables.Seasons;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class GeneralPreferenceFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener
    {
        private Preference mSeason;
        private Cursor mSeasonsData;

        private SharedPreferences mDefaultSharedPreferences;

        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            final Context appContext = getActivity().getApplicationContext();

            mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext);

            mSeasonsData = Database.getInstance(appContext).getAllRecords(Seasons.TABLE_NAME,
                    Seasons.DEFAULT_SORT_ORDER);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);

            mSeason = findPreference(getString(R.string.pref_key_season));
            if (mSeasonsData != null)
            {
                final int whichSeason = mDefaultSharedPreferences.getInt(
                        getString(R.string.pref_key_season), 0);
                mSeasonsData.moveToPosition(whichSeason);
                mSeason.setSummary(mSeasonsData.getString(Seasons.COL_ID_NAME));
            }
            mSeason.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override
                public boolean onPreferenceClick(final Preference preference)
                {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Choose season").setSingleChoiceItems(mSeasonsData,
                            mSeasonsData.getPosition(), Seasons.COL_NAME_NAME,
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(final DialogInterface dialog, final int which)
                                {
                                    mDefaultSharedPreferences.edit().putInt(
                                            getString(R.string.pref_key_season), which).apply();
                                }
                            }).setPositiveButton(android.R.string.ok, null).create().show();
                    return true;
                }
            });

            mDefaultSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause()
        {
            super.onPause();
            mDefaultSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences,
                final String key)
        {
            if (getString(R.string.pref_key_season).equalsIgnoreCase(key))
            {
                mSeasonsData.moveToPosition(sharedPreferences.getInt(key, 0));
                mSeason.setSummary(mSeasonsData.getString(Seasons.COL_ID_NAME));
            }
        }
    }
}
