package android.personal.fixtures.adapters;

import android.database.Cursor;
import android.personal.fixtures.R;
import android.personal.fixtures.database.tables.Seasons;
import android.personal.fixtures.fragments.SeasonsFragment.OnSeasonListInteractionListener;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Cursor} and makes a call to the
 * specified {@link OnSeasonListInteractionListener}.
 */
public class SeasonRecyclerViewAdapter
        extends RecyclerView.Adapter<SeasonRecyclerViewAdapter.ViewHolder>
{
    private static final String TAG = SeasonRecyclerViewAdapter.class.getSimpleName();

    private final Cursor mSeasons;
    private final int mItemCount;
    private final OnSeasonListInteractionListener mListener;

    public SeasonRecyclerViewAdapter(Cursor seasons, OnSeasonListInteractionListener listener)
    {
        mSeasons = seasons;
        mItemCount = mSeasons.getCount();
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.seasons_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        Log.v(TAG, "bind view for position " + position);
        mSeasons.moveToPosition(position);
        holder.mSeasonId = mSeasons.getLong(0);
        holder.mIdView.setText(String.valueOf(position + 1));
        holder.mNameView.setText(mSeasons.getString(Seasons.COL_ID_NAME));

        holder.mView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (null != mListener)
                {
                    // Notify the active callbacks interface (the activity, if the fragment is
                    // attached to one) that an item has been selected.
                    mListener.onSeasonSelected(holder.mSeasonId);
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
        public long mSeasonId;
        final View mView;
        final TextView mIdView;
        final TextView mNameView;

        ViewHolder(View view)
        {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.item_number);
            mNameView = view.findViewById(R.id.season_name);
        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
