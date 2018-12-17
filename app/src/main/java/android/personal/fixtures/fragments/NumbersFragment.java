package android.personal.fixtures.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.personal.fixtures.R;
import android.personal.fixtures.adapters.StatisticsRecyclerViewAdapter;
import android.personal.fixtures.database.Database;
import android.personal.fixtures.database.tables.Seasons;
import android.personal.fixtures.database.tables.Statistics;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * A fragment representing a list of statistical numbers, e.g. the number of games without a win.
 */
public class NumbersFragment extends Fragment
{
    private static final String TAG = NumbersFragment.class.getSimpleName();

    private static final String LEAGUE_FILTER = "League";
    private static final String ALL_FILTER = "All";

    private RecyclerView mRecyclerView;
    private Cursor mStatistics;
    private Database mDatabase;
    private String[] mGameTypes;
    private String[] mFilters;

    /* Filters */
    private long mSeasonId;
    private boolean mLeagueGamesOnly;
    private String mFilter;

    /* Item selected listeners */
    private AdapterView.OnItemSelectedListener mOnGameTypeSelectedListener =
            new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(final AdapterView<?> parent, final View view,
                        final int position, final long id)
                {
                    Log.d(TAG, "game type selected");
                    mLeagueGamesOnly = LEAGUE_FILTER.equalsIgnoreCase(mGameTypes[position]);
                    updateStatistics();
                }

                @Override
                public void onNothingSelected(final AdapterView<?> parent)
                {
                }
            };
    private AdapterView.OnItemSelectedListener mOnFilterSelectedListener =
            new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(final AdapterView<?> parent, final View view,
                        final int position, final long id)
                {
                    Log.d(TAG, "filter selected");
                    mFilter = mFilters[position];
                    updateStatistics();
                }

                @Override
                public void onNothingSelected(final AdapterView<?> parent)
                {
                }
            };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon
     * screen orientation changes).
     */
    public NumbersFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        mDatabase = Database.getInstance(getActivity().getApplicationContext());

        final int seasonIndex = PreferenceManager.getDefaultSharedPreferences(
                getActivity().getApplicationContext()).getInt(getString(R.string.pref_key_season),
                0);
        final Cursor seasonCursor = mDatabase.getAllRecords(Seasons.TABLE_NAME,
                Seasons.DEFAULT_SORT_ORDER);
        if (seasonCursor != null)
        {
            if (seasonCursor.moveToPosition(seasonIndex))
            {
                mSeasonId = seasonCursor.getLong(0);
            }
            seasonCursor.close();
        }

        mStatistics = null;

        mGameTypes = getResources().getStringArray(R.array.game_type_filters);
        mFilters = getResources().getStringArray(R.array.numbers_filters);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView");

        final View view = inflater.inflate(R.layout.fragment_numbers, container, false);

        // Set up the game type filter
        final Spinner gameTypeSpinner = view.findViewById(R.id.gameTypeSpinner);
        gameTypeSpinner.setAdapter(
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                        android.R.id.text1, mGameTypes));
        gameTypeSpinner.setOnItemSelectedListener(mOnGameTypeSelectedListener);

        // Set up the games filter
        final Spinner filterSpinner = view.findViewById(R.id.filterSpinner);
        filterSpinner.setAdapter(
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                        android.R.id.text1, mFilters));
        filterSpinner.setOnItemSelectedListener(mOnFilterSelectedListener);

        // Get the 2 filters and use them to get the statistics and fill the table
        mLeagueGamesOnly = LEAGUE_FILTER.equalsIgnoreCase(
                (String)gameTypeSpinner.getSelectedItem());
        mFilter = (String)filterSpinner.getSelectedItem();

        // Set up the list
        final View list = view.findViewById(R.id.numericalStatsList);
        if (list instanceof RecyclerView)
        {
            final Context context = list.getContext();
            mRecyclerView = (RecyclerView)list;
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            mRecyclerView.setAdapter(new StatisticsRecyclerViewAdapter(mStatistics));
        }

        return view;
    }

    private void updateStatistics()
    {
        Log.d(TAG, "updateStatistics, selected filters: season(" + mSeasonId + ") game type(" +
                (mLeagueGamesOnly ? "league games only)" : "all games)") + " filter(" + mFilter +
                ")");
        mStatistics = mDatabase.getSelection(Statistics.TABLE_NAME,
                Statistics.COL_NAME_SEASON_ID + "=? AND " + Statistics.COL_NAME_GAME_TYPE +
                        "=? AND " + Statistics.COL_NAME_FILTER + "=?",
                new String[]{String.valueOf(mSeasonId),
                        mLeagueGamesOnly ? LEAGUE_FILTER : ALL_FILTER, mFilter},
                BaseColumns._ID + " ASC");
//        DatabaseUtils.dumpCursor(mStatistics);
        mRecyclerView.setAdapter(new StatisticsRecyclerViewAdapter(mStatistics));
    }
}
