package android.personal.fixtures.adapters;

import android.database.Cursor;
import android.personal.fixtures.R;
import android.personal.fixtures.database.tables.Clubs;
import android.personal.fixtures.fragments.ClubsFragment.OnClubListInteractionListener;
import android.provider.BaseColumns;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Cursor} and makes a call to the
 * specified {@link OnClubListInteractionListener}.
 */
public class ClubRecyclerViewAdapter
        extends RecyclerView.Adapter<ClubRecyclerViewAdapter.ViewHolder>
{
    private static final String TAG = ClubRecyclerViewAdapter.class.getSimpleName();

    private final Cursor mClubs;
    private final int mItemCount;
    private final OnClubListInteractionListener mListener;

    public ClubRecyclerViewAdapter(Cursor clubs, OnClubListInteractionListener listener)
    {
        mClubs = clubs;
        mItemCount = mClubs.getCount();
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.clubs_list_item,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        Log.v(TAG, "bind view for position " + position);
        mClubs.moveToPosition(position);
        holder.mClubId = mClubs.getLong(mClubs.getColumnIndex(BaseColumns._ID));
        holder.mIdView.setText(String.valueOf(position + 1));
        holder.mFullNameView.setText(
                mClubs.getString(mClubs.getColumnIndex(Clubs.COL_NAME_FULL_NAME)));
        holder.mShortNameView.setText(
                mClubs.getString(mClubs.getColumnIndex(Clubs.COL_NAME_SHORT_NAME)));
        holder.mCodeView.setText(mClubs.getString(mClubs.getColumnIndex(Clubs.COL_NAME_CODE)));
        holder.mIsLeagueView.setVisibility(
                mClubs.getInt(mClubs.getColumnIndex(Clubs.COL_NAME_IS_LEAGUE)) == 1 ? View.VISIBLE :
                        View.INVISIBLE);

        holder.mView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (null != mListener)
                {
                    // Notify the active callbacks interface (the activity, if the fragment is
                    // attached to one) that an item has been selected.
                    mListener.onClubSelected(holder.mClubId);
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
        public long mClubId;
        final View mView;
        final TextView mIdView;
        final TextView mFullNameView;
        final TextView mShortNameView;
        final TextView mCodeView;
        final TextView mIsLeagueView;

        ViewHolder(View view)
        {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.item_number);
            mFullNameView = view.findViewById(R.id.full_name);
            mShortNameView = view.findViewById(R.id.short_name);
            mCodeView = view.findViewById(R.id.code);
            mIsLeagueView = view.findViewById(R.id.is_league_view);
        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + mFullNameView.getText() + "'";
        }
    }
}
