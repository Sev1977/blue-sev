package android.personal.fixtures.adapters;

import android.database.Cursor;
import android.personal.fixtures.R;
import android.personal.fixtures.database.tables.Players;
import android.personal.fixtures.fragments.PlayersFragment.OnPlayersListInteractionListener;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Cursor} and makes a call to the specified
 * {@link OnPlayersListInteractionListener}. TODO: Replace the implementation with code for your
 * data type.
 */
public class PlayerRecyclerViewAdapter
        extends RecyclerView.Adapter<PlayerRecyclerViewAdapter.ViewHolder>
{
    private static final String TAG = PlayerRecyclerViewAdapter.class.getSimpleName();

    private final Cursor mPlayers;
    private final int mItemCount;
    private final OnPlayersListInteractionListener mListener;

    public PlayerRecyclerViewAdapter(Cursor players, OnPlayersListInteractionListener listener)
    {
        mPlayers = players;
        mItemCount = mPlayers.getCount();
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.players_list_item,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        Log.v(TAG, "bind view for position " + position);
        mPlayers.moveToPosition(position);
        holder.mPlayerId = mPlayers.getLong(0);
        holder.mIdView.setText(String.valueOf(position + 1));
        holder.mNameView.setText(holder.mView.getContext().getString(R.string.player_name,
                mPlayers.getString(mPlayers.getColumnIndex(Players.COL_NAME_FORENAME)),
                mPlayers.getString(mPlayers.getColumnIndex(Players.COL_NAME_SURNAME))));
        holder.mCurrent.setVisibility(
                mPlayers.getInt(mPlayers.getColumnIndex(Players.COL_NAME_CURRENT)) == 1 ?
                        View.VISIBLE : View.INVISIBLE);

        holder.mView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (null != mListener)
                {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onPlayerSelected(holder.mPlayerId);
                }
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return mItemCount;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public long mPlayerId;
        final View mView;
        final TextView mIdView;
        final TextView mNameView;
        final ImageView mCurrent;

        ViewHolder(View view)
        {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.item_number);
            mNameView = view.findViewById(R.id.player_name);
            mCurrent = view.findViewById(R.id.is_current);
        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
