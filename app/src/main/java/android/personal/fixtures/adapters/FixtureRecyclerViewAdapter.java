package android.personal.fixtures.adapters;

import android.content.Context;
import android.database.Cursor;
import android.personal.fixtures.R;
import android.personal.fixtures.database.tables.Fixtures;
import android.personal.fixtures.fragments.FixturesFragment.OnFixturesListInteractionListener;
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
 * specified {@link OnFixturesListInteractionListener}.
 */
public class FixtureRecyclerViewAdapter
        extends RecyclerView.Adapter<FixtureRecyclerViewAdapter.ViewHolder>
{
    private static final String TAG = FixtureRecyclerViewAdapter.class.getSimpleName();

    private final Cursor mFixtures;
    private final int mItemCount;
    private final OnFixturesListInteractionListener mListener;
    private SimpleDateFormat mDateFormat;

    public FixtureRecyclerViewAdapter(final Context context, final Cursor fixtures,
            final OnFixturesListInteractionListener listener)
    {
        Log.v(TAG, "Constructor");
        mFixtures = fixtures;
        mItemCount = mFixtures.getCount();
        mListener = listener;
        mDateFormat = new SimpleDateFormat(context.getString(R.string.long_date_format), Locale.UK);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.fixture_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        Log.d(TAG, "onBindViewHolder, position: " + position);
        mFixtures.moveToPosition(position);

        holder.mFixtureId = mFixtures.getLong(0);

        final int dateTime = mFixtures.getInt(Fixtures.COL_ID_DATE);
        holder.mDateTimeView.setText(
                mDateFormat.format(new Date(TimeUnit.SECONDS.toMillis(dateTime))));
        holder.mCompetitionView.setText(mFixtures.getString(Fixtures.COL_ID_COMPETITION));
        final Context context = holder.mView.getContext();
        final boolean isHomeMatch = context.getString(R.string.home).equalsIgnoreCase(
                mFixtures.getString(Fixtures.COL_ID_VENUE));
        if (isHomeMatch)
        {
            holder.mHomeTeamView.setText(context.getString(R.string.cardiff_city));
            holder.mHomeTeamView.setTextAppearance(R.style.matchListItemCardiff);

            holder.mAwayTeamView.setText(mFixtures.getString(Fixtures.COL_ID_OPPOSITION));
            holder.mAwayTeamView.setTextAppearance(R.style.matchListItemOpposition);
        }
        else
        {
            holder.mHomeTeamView.setText(mFixtures.getString(Fixtures.COL_ID_OPPOSITION));
            holder.mHomeTeamView.setTextAppearance(R.style.matchListItemOpposition);

            holder.mAwayTeamView.setText(context.getString(R.string.cardiff_city));
            holder.mAwayTeamView.setTextAppearance(R.style.matchListItemCardiff);
        }

        holder.mView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mListener != null)
                {
                    // Notify the active callbacks interface (the activity, if the fragment is
                    // attached to one) that an item has been selected.
                    mListener.onFixtureSelected(holder.mFixtureId);
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
        final TextView mAwayTeamView;

        ViewHolder(View view)
        {
            super(view);
            mView = view;
            mDateTimeView = view.findViewById(R.id.fixtureDateTime);
            mCompetitionView = view.findViewById(R.id.fixtureCompetition);
            mHomeTeamView = view.findViewById(R.id.fixtureHomeTeam);
            mAwayTeamView = view.findViewById(R.id.fixtureAwayTeam);
        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + mDateTimeView.getText() + "'";
        }
    }
}
