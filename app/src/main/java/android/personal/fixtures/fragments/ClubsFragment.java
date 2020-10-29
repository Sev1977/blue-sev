package android.personal.fixtures.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.personal.fixtures.R;
import android.personal.fixtures.adapters.ClubRecyclerViewAdapter;
import android.personal.fixtures.database.Database;
import android.personal.fixtures.database.helpers.ClubsHelper;
import android.personal.fixtures.database.tables.Clubs;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

/**
 * A fragment representing a list of Clubs.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnClubListInteractionListener}
 * interface.
 */
public class ClubsFragment extends Fragment
{
    private static final String TAG = ClubsFragment.class.getSimpleName();

    private Cursor mClubs;
    private RecyclerView mList;
    private OnClubListInteractionListener mListener;
    private Database mDatabase;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon
     * screen orientation changes).
     */
    public ClubsFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mDatabase = Database.getInstance(getContext());

        mClubs = mDatabase.getAllRecords(Clubs.TABLE_NAME, Clubs.DEFAULT_SORT_ORDER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_club_list, container, false);

        // Set the adapter
        final View list = view.findViewById(R.id.clubs_recycler_view);
        if (list instanceof RecyclerView)
        {
            Context context = list.getContext();
            mList = (RecyclerView)list;
            mList.setLayoutManager(new LinearLayoutManager(context));
            mList.setAdapter(new ClubRecyclerViewAdapter(mClubs, mListener));
        }

        ((Switch)view.findViewById(R.id.clubs_list_filter_switch)).setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(final CompoundButton compoundButton,
                            final boolean b)
                    {
                        if (b)
                        {
                            Snackbar.make(compoundButton, "Only show current league opponents",
                                    Snackbar.LENGTH_SHORT).show();
                            mClubs = ClubsHelper.getAllLeagueClubs(mDatabase);
                        }
                        else
                        {
                            Snackbar.make(compoundButton, "Show all clubs", Snackbar.LENGTH_SHORT)
                                    .show();
                            mClubs = mDatabase.getAllRecords(Clubs.TABLE_NAME,
                                    Clubs.DEFAULT_SORT_ORDER);
                        }
                        mList.setAdapter(new ClubRecyclerViewAdapter(mClubs, mListener));
                    }
                });

        return view;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnClubListInteractionListener)
        {
            mListener = (OnClubListInteractionListener)context;
        }
        else
        {
            throw new RuntimeException(
                    context.toString() + " must implement OnClubListInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
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
    public interface OnClubListInteractionListener
    {
        void onClubSelected(final long id);
    }
}
