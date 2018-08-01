package android.personal.fixtures.adapters;

import android.database.Cursor;
import android.personal.fixtures.R;
import android.personal.fixtures.database.tables.Competitions;
import android.personal.fixtures.fragments.CompetitionsFragment.OnCompetitionsListInteractionListener;
import android.provider.BaseColumns;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Cursor} and makes a call to the
 * specified {@link OnCompetitionsListInteractionListener}.
 */
public class CompetitionRecyclerViewAdapter
        extends RecyclerView.Adapter<CompetitionRecyclerViewAdapter.ViewHolder>
{
    private static final String TAG = CompetitionRecyclerViewAdapter.class.getSimpleName();

    private final Cursor mCompetitions;
    private final int mItemCount;
    private final OnCompetitionsListInteractionListener mListener;

    public CompetitionRecyclerViewAdapter(Cursor competitions,
            OnCompetitionsListInteractionListener listener)
    {
        mCompetitions = competitions;
        mItemCount = mCompetitions.getCount();
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.competitions_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        Log.v(TAG, "bind view for position " + position);
        mCompetitions.moveToPosition(position);
        holder.mCompetitionId = mCompetitions.getLong(
                mCompetitions.getColumnIndex(BaseColumns._ID));
        holder.mIdView.setText(String.valueOf(position + 1));
        holder.mFullNameView.setText(mCompetitions
                .getString(mCompetitions.getColumnIndex(Competitions.COL_NAME_FULL_NAME)));
        holder.mShortNameView.setText(mCompetitions
                .getString(mCompetitions.getColumnIndex(Competitions.COL_NAME_SHORT_NAME)));

        holder.mView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (null != mListener)
                {
                    // Notify the active callbacks interface (the activity, if the fragment is
                    // attached to one) that an item has been selected.
                    mListener.onCompetitionSelected(holder.mCompetitionId);
                }
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return mItemCount;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public long mCompetitionId;
        final View mView;
        final TextView mIdView;
        final TextView mFullNameView;
        final TextView mShortNameView;

        ViewHolder(View view)
        {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.item_number);
            mFullNameView = view.findViewById(R.id.full_name);
            mShortNameView = view.findViewById(R.id.short_name);
        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + mFullNameView.getText() + "'";
        }
    }
}
