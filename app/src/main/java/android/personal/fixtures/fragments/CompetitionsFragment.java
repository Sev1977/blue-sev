package android.personal.fixtures.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.personal.fixtures.R;
import android.personal.fixtures.adapters.CompetitionRecyclerViewAdapter;
import android.personal.fixtures.database.Database;
import android.personal.fixtures.database.tables.Competitions;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CompetitionsFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mCompetitions = Database.getInstance(getActivity().getApplicationContext()).getAllRecords(
                Competitions.TABLE_NAME, Competitions.DEFAULT_SORT_ORDER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_club_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView)
        {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView)view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new CompetitionRecyclerViewAdapter(mCompetitions, mListener));
        }

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
     *     Communicating with Other Fragments</a> for more information.
     */
    public interface OnCompetitionsListInteractionListener
    {
        void onCompetitionSelected(final long id);
    }
}
