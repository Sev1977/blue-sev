package android.personal.fixtures.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.personal.fixtures.R;
import android.personal.fixtures.adapters.CompetitionRecyclerViewAdapter;
import android.personal.fixtures.database.Database;
import android.personal.fixtures.database.tables.Competitions;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A fragment representing a list of Competitions.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnCompetitionsListInteractionListener}
 * interface.
 */
public class CompetitionsFragment extends Fragment
{
    private static final String TAG = CompetitionsFragment.class.getSimpleName();

    private Cursor mCompetitions;
    private OnCompetitionsListInteractionListener mListener;
    private Database mDatabase;
    private RecyclerView mList;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon
     * screen orientation changes).
     */
    public CompetitionsFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mDatabase = Database.getInstance(getActivity());

        mCompetitions = mDatabase.getAllRecords(Competitions.TABLE_NAME,
                Competitions.DEFAULT_SORT_ORDER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_competition_list, container, false);

        // Set the adapter
        final View list = view.findViewById(R.id.competitions_recycler_view);
        if (list instanceof RecyclerView)
        {
            mList = (RecyclerView)list;
            mList.setLayoutManager(new LinearLayoutManager(view.getContext()));
            mList.setAdapter(new CompetitionRecyclerViewAdapter(mCompetitions, mListener));
        }

        final Context context = view.getContext();
        final String[] filterItems = new String[]{context.getString(R.string.comps_filter_all),
                context.getString(R.string.comps_filter_leagues), context.getString(
                R.string.comps_filter_cups)};

        view.findViewById(R.id.competitions_list_filter).setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View view)
                    {
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                        dialogBuilder.setTitle("Filter").setNegativeButton(android.R.string.cancel,
                                null).setItems(filterItems, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, final int i)
                            {
                                ((TextView)view.findViewById(R.id.competitions_filter_label))
                                        .setText(filterItems[i]);
                                switch (i)
                                {
                                    case 0: // All
                                        mCompetitions = mDatabase.getAllRecords(
                                                Competitions.TABLE_NAME,
                                                Competitions.DEFAULT_SORT_ORDER);
                                        break;

                                    case 1: // Leagues
                                        mCompetitions = mDatabase.getColumnsForSelection(
                                                Competitions.TABLE_NAME, null,
                                                Competitions.COL_NAME_IS_LEAGUE + "=1", null,
                                                Competitions.DEFAULT_SORT_ORDER);
                                        break;

                                    case 2:
                                        mCompetitions = mDatabase.getColumnsForSelection(
                                                Competitions.TABLE_NAME, null,
                                                Competitions.COL_NAME_IS_LEAGUE + "=0", null,
                                                Competitions.DEFAULT_SORT_ORDER);
                                        break;
                                }
                                mList.setAdapter(new CompetitionRecyclerViewAdapter(mCompetitions,
                                        mListener));
                            }
                        }).create().show();
                    }
                });

        return view;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnCompetitionsListInteractionListener)
        {
            mListener = (OnCompetitionsListInteractionListener)context;
        }
        else
        {
            throw new RuntimeException(
                    context.toString() + " must implement OnCompetitionListInteractionListener");
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
    public interface OnCompetitionsListInteractionListener
    {
        void onCompetitionSelected(final long id);
    }
}
