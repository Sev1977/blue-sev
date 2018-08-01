package android.personal.fixtures.adapters;

import android.content.Context;
import android.database.Cursor;
import android.personal.fixtures.R;
import android.personal.fixtures.database.tables.Fixtures;
import android.personal.fixtures.fragments.ResultsFragment.OnResultsListInteractionListener;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Cursor} and makes a call to the
 * specified {@link OnResultsListInteractionListener}.
 */
public class ResultRecyclerViewAdapter
        extends RecyclerView.Adapter<ResultRecyclerViewAdapter.ViewHolder>
{
    private static final String TAG = ResultRecyclerViewAdapter.class.getSimpleName();

    private final Cursor mResults;
    private final int mItemCount;
    private final OnResultsListInteractionListener mListener;
    private SimpleDateFormat mDateFormat;

    public ResultRecyclerViewAdapter(final Context context, final Cursor results,
            final OnResultsListInteractionListener listener)
    {
        Log.v(TAG, "Constructor");
        mResults = results;
        mItemCount = mResults.getCount();
        mListener = listener;
        mDateFormat = new SimpleDateFormat(context.getString(R.string.long_date_format), Locale.UK);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_list_item,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        Log.d(TAG, "onBindViewHolder, position: " + position);
        mResults.moveToPosition(position);

        holder.mFixtureId = mResults.getLong(0);

        final int dateTime = mResults.getInt(Fixtures.COL_ID_DATE);
        holder.mDateTimeView.setText(
                mDateFormat.format(new Date(TimeUnit.SECONDS.toMillis(dateTime))));
        holder.mCompetitionView.setText(mResults.getString(Fixtures.COL_ID_COMPETITION));
        final Context context = holder.mView.getContext();
        final boolean isHomeMatch = context.getString(R.string.home).equalsIgnoreCase(
                mResults.getString(Fixtures.COL_ID_VENUE));
        if (isHomeMatch)
        {
            holder.mHomeTeamView.setText(context.getString(R.string.cardiff_city));
            holder.mHomeTeamView.setTextAppearance(R.style.fixtureListItemCardiff);
            holder.mHomeTeamScore.setText(mResults.getString(Fixtures.COL_ID_GOALS_SCORED));

            holder.mAwayTeamView.setText(mResults.getString(Fixtures.COL_ID_OPPOSITION));
            holder.mAwayTeamView.setTextAppearance(R.style.fixtureListItemOpposition);
            holder.mAwayTeamScore.setText(
                    String.valueOf(mResults.getInt(Fixtures.COL_ID_GOALS_CONCEDED)));
        }
        else
        {
            holder.mHomeTeamView.setText(mResults.getString(Fixtures.COL_ID_OPPOSITION));
            holder.mHomeTeamView.setTextAppearance(R.style.fixtureListItemOpposition);
            holder.mHomeTeamScore.setText(
                    String.valueOf(mResults.getInt(Fixtures.COL_ID_GOALS_CONCEDED)));

            holder.mAwayTeamView.setText(context.getString(R.string.cardiff_city));
            holder.mAwayTeamView.setTextAppearance(R.style.fixtureListItemCardiff);
            holder.mAwayTeamScore.setText(mResults.getString(Fixtures.COL_ID_GOALS_SCORED));
        }

        holder.mEditAction.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View view)
            {
                if (mListener != null)
                {
                    mListener.onEditResult(holder.mFixtureId);
                }
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mListener != null)
                {
                    mListener.onResultSelected(holder.mFixtureId);
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
        public long mFixtureId;
        final View mView;
        final TextView mDateTimeView;
        final TextView mCompetitionView;
        final TextView mHomeTeamView;
        final TextView mHomeTeamScore;
        final TextView mAwayTeamView;
        final TextView mAwayTeamScore;
        final ImageView mEditAction;

        ViewHolder(View view)
        {
            super(view);
            mView = view;
            mDateTimeView = view.findViewById(R.id.resultDateTime);
            mCompetitionView = view.findViewById(R.id.resultCompetition);
            mHomeTeamView = view.findViewById(R.id.resultHomeTeam);
            mHomeTeamScore = view.findViewById(R.id.resultHomeScore);
            mAwayTeamView = view.findViewById(R.id.resultAwayTeam);
            mAwayTeamScore = view.findViewById(R.id.resultAwayScore);
            mEditAction = view.findViewById(R.id.resultEditAction);
        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + mDateTimeView.getText() + "'";
        }
    }
}
