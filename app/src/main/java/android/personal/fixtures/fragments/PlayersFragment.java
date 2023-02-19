package android.personal.fixtures.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.personal.fixtures.R;
import android.personal.fixtures.Settings;
import android.personal.fixtures.adapters.PlayerRecyclerViewAdapter;
import android.personal.fixtures.database.Database;
import android.personal.fixtures.database.helpers.PlayersHelper;
import android.personal.fixtures.database.tables.Players;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

/**
 * A fragment representing a list of players.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnPlayersListInteractionListener}
 * interface.
 */
public class PlayersFragment extends Fragment
{
    private static final String TAG = PlayersFragment.class.getSimpleName();

    private Cursor mPlayers;
    private RecyclerView mList;
    private OnPlayersListInteractionListener mListener;
    private Database mDatabase;
    private Switch mFilterSwitch;

    private final CompoundButton.OnCheckedChangeListener mCheckedChangeListener =
            new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(final CompoundButton compoundButton,
                        final boolean isChecked)
                {
                    UpdateFilter(isChecked);
                    if (isChecked)
                        Snackbar.make(compoundButton, "Only show current players",
                                Snackbar.LENGTH_SHORT).show();
                    else
                        Snackbar.make(compoundButton, "Show all players",
                                Snackbar.LENGTH_SHORT).show();
                }
            };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment
     * (e.g. upon screen orientation changes).
     */
    public PlayersFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mDatabase = Database.getInstance(getContext());
        mPlayers = mDatabase.getAllRecords(Players.TABLE_NAME, Players.NAME_SORT_ORDER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        Log.v(TAG, "onCreateView");
        final View view = inflater.inflate(R.layout.fragment_player_list, container, false);

        // Set the adapter
        final View list = view.findViewById(R.id.players_recycler_view);
        if (list instanceof RecyclerView)
        {
            mList = (RecyclerView)list;
            mList.setLayoutManager(new LinearLayoutManager(list.getContext()));
            mList.setAdapter(new PlayerRecyclerViewAdapter(mPlayers, mListener));
        }

        mFilterSwitch = view.findViewById(R.id.players_list_filter_switch);
        final boolean isChecked = Settings.showCurrentPlayersOnly(getContext());
        mFilterSwitch.setChecked(isChecked);
        UpdateFilter(isChecked);
        mFilterSwitch.setOnCheckedChangeListener(mCheckedChangeListener);

        return view;
    }

    @Override
    public void onAttach(Context context)
    {
        Log.v(TAG, "onAttach");
        super.onAttach(context);
        if (context instanceof OnPlayersListInteractionListener)
        {
            mListener = (OnPlayersListInteractionListener)context;
        }
        else
        {
            throw new RuntimeException(
                    context.toString() + " must implement OnPlayerListInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        Log.v(TAG, "onDetach");
        super.onDetach();
        mListener = null;
        Settings.setShowCurrentPlayersOnly(getContext(), mFilterSwitch.isChecked());
    }

    /**
     *
     * @param set
     */
    private void UpdateFilter(final boolean set)
    {
        if (set)
        {
            mPlayers = PlayersHelper.getCurrentPlayers(mDatabase);
        }
        else
        {
            mPlayers = mDatabase.getAllRecords(Players.TABLE_NAME, Players.NAME_SORT_ORDER);
        }

        mList.setAdapter(new PlayerRecyclerViewAdapter(mPlayers, mListener));
    }

    /**
     * This interface must be implemented by activities that contain this fragment to allow an
     * interaction in this fragment to be communicated to the activity and potentially other
     * fragments contained in that activity.
     * <p/>
     * See the Android Training lesson
     * <a href="http://developer.android.com/training/basics/fragments/communicating.html">
     * Communicating with Other Fragments</a> for more information.
     */
    public interface OnPlayersListInteractionListener
    {
        void onPlayerSelected(final long id);
    }
}
