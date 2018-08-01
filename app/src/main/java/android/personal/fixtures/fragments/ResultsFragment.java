package android.personal.fixtures.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.personal.fixtures.R;
import android.personal.fixtures.adapters.ResultRecyclerViewAdapter;
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
 * A fragment representing a list of Results.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnResultsListInteractionListener}
 * interface.
 */
public class ResultsFragment extends Fragment
{
    private static final String TAG = ResultsFragment.class.getSimpleName();

    private Cursor mResults;
    private OnResultsListInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon
     * screen orientation changes).
     */
    public ResultsFragment()
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
        final View view = inflater.inflate(R.layout.fragment_result_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView)
        {
            final Context context = view.getContext();
            final RecyclerView recyclerView = (RecyclerView)view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(
                    new ResultRecyclerViewAdapter(getContext(), mResults, mListener));
        }

        return view;
    }


    @Override
    public void onAttach(Context context)
    {
        Log.v(TAG, "onAttach");
        super.onAttach(context);
        if (context instanceof OnResultsListInteractionListener)
        {
            mListener = (OnResultsListInteractionListener)context;
        }
        else
        {
            throw new RuntimeException(
                    context.toString() + " must implement OnResultsListInteractionListener");
        }

        mResults = FixturesHelper.getAllResults(
                Database.getInstance(getActivity().getApplicationContext()));
        if (mResults != null)
        {
            Log.i(TAG, "number of results: " + mResults.getCount());
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
    public interface OnResultsListInteractionListener
    {
        void onResultSelected(final long id);
        void onEditResult(final long id);
    }
}
