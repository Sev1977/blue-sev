package android.personal.fixtures.adapters;

import android.database.Cursor;
import android.personal.fixtures.R;
import android.personal.fixtures.fragments.OppoChoiceFragment.GridInteractionListener;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Cursor} and makes a call to the specified
 * {@link GridInteractionListener }.
 */
public class ClubPickerGridAdapter extends RecyclerView.Adapter<ClubPickerGridAdapter.ViewHolder>
{
    private static final String TAG = ClubPickerGridAdapter.class.getSimpleName();

    private final Cursor mItems;
    private final int mItemCount;
    private final GridInteractionListener mListener;

    public ClubPickerGridAdapter(Cursor teams, GridInteractionListener listener)
    {
        if (teams == null || listener == null)
            Log.e(TAG, "Invalid parameters");
        mItems = teams;
        mItemCount = mItems.getCount();
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.club_picker_grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        Log.v(TAG, "bind view for position " + position);
        mItems.moveToPosition(position);
        holder.mItemId = mItems.getLong(0);
        if (mItems.getColumnCount() == 2)
        {
            holder.mTextView.setText(mItems.getString(1));
        }
        else
        {
            Log.e(TAG, "Trying to bind to the wrong cursor item");
        }

        holder.mView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (null != mListener)
                {
                    // Notify the active callbacks interface (the activity, if the fragment is
                    // attached to one) that an item has been selected.
                    mListener.onTeamSelected(holder.mItemId);
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
        long mItemId;
        final View mView;
        final TextView mTextView;

        ViewHolder(View view)
        {
            super(view);
            mView = view;
            mTextView = view.findViewById(R.id.item_name);
        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + mTextView.getText() + "'";
        }
    }
}
