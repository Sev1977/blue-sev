package android.personal.fixtures.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.personal.fixtures.R;
import android.personal.fixtures.adapters.FixtureRecyclerViewAdapter;
import android.personal.fixtures.database.Database;
import android.personal.fixtures.database.FixturesHelper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A fragment representing a list of Fixtures.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFixturesListInteractionListener}
 * interface.
 */
public class FixturesFragment extends Fragment
{
    private static final String TAG = FixturesFragment.class.getSimpleName();

    private Cursor mFixtures;
    private OnFixturesListInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon
     * screen orientation changes).
     */
    public FixturesFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_fixture_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView)
        {
            final Context context = view.getContext();
            final RecyclerView recyclerView = (RecyclerView)view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(
                    new FixtureRecyclerViewAdapter(getContext(), mFixtures, mListener));
        }

        return view;
    }


    @Override
    public void onAttach(Context context)
    {
        Log.v(TAG, "onAttach");
        super.onAttach(context);
        if (context instanceof OnFixturesListInteractionListener)
        {
            mListener = (OnFixturesListInteractionListener)context;
        }
        else
        {
            throw new RuntimeException(
                    context.toString() + " must implement OnFixturesListInteractionListener");
        }

        mFixtures = FixturesHelper.getAllFixtures(
                Database.getInstance(getActivity().getApplicationContext()));
        if (mFixtures != null)
        {
            Log.i(TAG, "number of fixtures: " + mFixtures.getCount());
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
    public interface OnFixturesListInteractionListener
    {
        void onFixtureSelected(final long id);
        void onEditClicked(final long id);
    }
}
