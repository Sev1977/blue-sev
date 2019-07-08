package android.personal.fixtures.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.personal.fixtures.R;
import android.personal.fixtures.adapters.ResultRecyclerViewAdapter;
import android.personal.fixtures.database.Database;
import android.personal.fixtures.database.helpers.FixturesHelper;
import android.personal.fixtures.interfaces.MainActivityInteraction;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

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
    private boolean mShowLeagueOnly;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon
     * screen orientation changes).
     */
    public ResultsFragment()
    {
    }

    @Override
    public void onAttach(Context context)
    {
        Log.d(TAG, "onAttach");
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

        mShowLeagueOnly = ((MainActivityInteraction)getActivity()).getOnlyShowLeagueFixtures();
        if (mShowLeagueOnly)
        {
            mResults = FixturesHelper.getLeagueResults(
                    Database.getInstance(getActivity().getApplicationContext()));
        }
        else
        {
            final Context appContext = getActivity().getApplicationContext();
            mResults = FixturesHelper.getAllResults(Database.getInstance(appContext), appContext);
        }
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView");
        final View view = inflater.inflate(R.layout.fragment_result_list, container, false);

        // Set the adapter
        final View list = view.findViewById(R.id.list);
        if (list instanceof RecyclerView)
        {
            final Context context = list.getContext();
            final RecyclerView recyclerView = (RecyclerView)list;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(
                    new ResultRecyclerViewAdapter(getContext(), mResults, mListener));
        }

        // setup the form views.
        final ArrayList<Integer> form = FixturesHelper.getRecentForm(
                Database.getInstance(getActivity().getApplicationContext()));
        if (form.size() > 0)
        {
            final ArrayList<ImageView> formViews = new ArrayList<>();
            formViews.add((ImageView)view.findViewById(R.id.resultsForm6));
            formViews.add((ImageView)view.findViewById(R.id.resultsForm5));
            formViews.add((ImageView)view.findViewById(R.id.resultsForm4));
            formViews.add((ImageView)view.findViewById(R.id.resultsForm3));
            formViews.add((ImageView)view.findViewById(R.id.resultsForm2));
            formViews.add((ImageView)view.findViewById(R.id.resultsForm1));
            for (int i = 0; i < form.size(); i++)
            {
                final Integer result = form.get(i);
                if (result == 3)
                {
                    formViews.get(i).setImageResource(R.drawable.form_win);
                }
                else if (result == 1)
                {
                    formViews.get(i).setImageResource(R.drawable.form_draw);
                }
                else
                {
                    formViews.get(i).setImageResource(R.drawable.form_defeat);
                }
            }
        }

        // Update the league/all label
        ((TextView)view.findViewById(R.id.results_filter_label)).setText(
                mShowLeagueOnly ? R.string.filter_lge : R.string.filter_all);

        return view;
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
