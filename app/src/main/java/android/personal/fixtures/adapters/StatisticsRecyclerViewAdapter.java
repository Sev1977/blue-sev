package android.personal.fixtures.adapters;

import android.database.Cursor;
import android.personal.fixtures.R;
import android.personal.fixtures.database.tables.Statistics;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Cursor}.
 */
public class StatisticsRecyclerViewAdapter
        extends RecyclerView.Adapter<StatisticsRecyclerViewAdapter.ViewHolder>
{
    private static final String TAG = StatisticsRecyclerViewAdapter.class.getSimpleName();

    private final Cursor mStatistics;
    private final int mItemCount;

    public StatisticsRecyclerViewAdapter(final Cursor statistics)
    {
        Log.v(TAG, "Constructor");
        mStatistics = statistics;
        mItemCount = (mStatistics == null) ? 0 : mStatistics.getCount();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.statistic_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        Log.d(TAG, "onBindViewHolder, position: " + position);
        mStatistics.moveToPosition(position);

        holder.mStatisticId = mStatistics.getLong(0);
        holder.mDescriptionView.setText(
                mStatistics.getString(mStatistics.getColumnIndex(Statistics.COL_NAME_DESCRIPTION)));
        holder.mValueView.setText(String.valueOf(
                mStatistics.getInt(mStatistics.getColumnIndex(Statistics.COL_NAME_VALUE))));
    }

    @Override
    public int getItemCount()
    {
        return mItemCount;
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        long mStatisticId;
        final View mView;
        final TextView mDescriptionView;
        final TextView mValueView;

        ViewHolder(View view)
        {
            super(view);
            mView = view;
            mDescriptionView = view.findViewById(R.id.statDescription);
            mValueView = view.findViewById(R.id.statValue);
        }
    }
}
