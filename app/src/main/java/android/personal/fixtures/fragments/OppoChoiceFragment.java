package android.personal.fixtures.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.personal.fixtures.R;
import android.personal.fixtures.adapters.ClubPickerGridAdapter;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A fragment representing a grid of teams for selection.
 * <p/>
 * Activities containing this fragment MUST implement the {@link GridInteractionListener}
 * interface.
 */
public class OppoChoiceFragment extends Fragment
{
    private static final String TAG = OppoChoiceFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private GridInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon
     * screen orientation changes).
     */
    public OppoChoiceFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_club_grid, container, false);

        if (view instanceof RecyclerView)
        {
            mRecyclerView = (RecyclerView)view;
            GridLayoutManager layoutManager = new GridLayoutManager(view.getContext(), 3);
            mRecyclerView.setLayoutManager(layoutManager);
        }
        else
        {
            Log.w(TAG, "Failed to create the RecyclerView");
        }

        return view;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof GridInteractionListener)
        {
            mListener = (GridInteractionListener)context;
        }
        else
        {
            throw new RuntimeException(
                    context.toString() + " must implement GridInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    public void setCursor(final Cursor items)
    {
        final ClubPickerGridAdapter adapter = new ClubPickerGridAdapter(items, mListener);
        mRecyclerView.setAdapter(adapter);
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
    public interface GridInteractionListener
    {
        void onTeamSelected(final long id);
    }
}
