package android.personal.fixtures.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.personal.fixtures.EditFixtureActivity;
import android.personal.fixtures.FixtureActivity;
import android.personal.fixtures.R;
import android.personal.fixtures.database.Database;
import android.personal.fixtures.database.helpers.ClubsHelper;
import android.personal.fixtures.database.tables.Fixtures;
import android.personal.fixtures.database.tables.Goals;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Use the {@link FixturePopupFragment#newInstance} factory method to create an instance of this
 * fragment.
 */
public class FixturePopupFragment extends DialogFragment
{
    public static final String EXTRA_FIXTURE_ID = "fixture_id";

    public static final int REQUEST_EDIT = 0;

    private Database mDatabase;

    private long mFixtureId = 0;
    private Cursor mFixture;

    public FixturePopupFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     *
     * @param fixtureId ID of the fixture to be viewed.
     * @return A new instance of fragment FixturePopupFragment.
     */
    public static FixturePopupFragment newInstance(long fixtureId)
    {
        final FixturePopupFragment fragment = new FixturePopupFragment();
        final Bundle args = new Bundle();
        args.putLong(EXTRA_FIXTURE_ID, fixtureId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mFixtureId = getArguments().getLong(EXTRA_FIXTURE_ID, 0);
        }
        mDatabase = Database.getInstance(getActivity().getApplicationContext());
        mFixture = mDatabase.getFullRecordWithId(Fixtures.TABLE_NAME, mFixtureId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        final View layout = inflater.inflate(R.layout.fragment_fixture_popup, container, false);
        layout.findViewById(R.id.editButton).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View view)
            {
                final Intent edit = new Intent(getActivity().getApplicationContext(),
                        EditFixtureActivity.class);
                edit.putExtra(EXTRA_FIXTURE_ID, mFixtureId);
                startActivityForResult(edit, REQUEST_EDIT);
            }
        });

        if (mFixture != null)
        {
            // Set the date
            final int unixTimestamp = mFixture.getInt(Fixtures.COL_ID_DATE);
            final Calendar calendar = Calendar.getInstance();
            final long now = calendar.getTimeInMillis(); // store the time now for later use.
            calendar.setTimeInMillis(TimeUnit.SECONDS.toMillis(unixTimestamp));
            ((TextView)layout.findViewById(R.id.dateAndTime)).setText(
                    new SimpleDateFormat(getString(R.string.long_date_format), Locale.UK)
                            .format(calendar.getTime()));

            // Set the competition
            ((TextView)layout.findViewById(R.id.competition)).setText(
                    mFixture.getString(Fixtures.COL_ID_COMPETITION));

            // Set the opposition
            final String shortName = mFixture.getString(Fixtures.COL_ID_OPPOSITION);
            final String fullName = ClubsHelper.getFullNameFromShortName(mDatabase, shortName);
            ((TextView)layout.findViewById(R.id.opposition)).setText(fullName);

            // Set home or away
            ((TextView)layout.findViewById(R.id.homeOrAway)).setText(
                    mFixture.getString(Fixtures.COL_ID_VENUE));
            if (calendar.getTimeInMillis() < now)
            {
                layout.findViewById(R.id.mainScore).setVisibility(View.VISIBLE);

                // Show the score, scorers and attendance.  If it's a fixture, the views will be empty.
                final Resources resources = getResources();

                // Set the score
                final int scored = mFixture.getInt(Fixtures.COL_ID_GOALS_SCORED);
                final int conceded = mFixture.getInt(Fixtures.COL_ID_GOALS_CONCEDED);
                TextView scoreView = layout.findViewById(R.id.score);
                scoreView.setText(getString(R.string.score_format, scored, conceded));
                int scoreColour = 0;
                if (scored > conceded)
                {
                    scoreColour = resources.getColor(R.color.result_win);
                }
                else if (scored < conceded)
                {
                    scoreColour = resources.getColor(R.color.result_defeat);
                }
                if (scoreColour != 0)
                {
                    scoreView.setTextColor(scoreColour);
                }

                // Set the goal scorers
                final StringBuilder builder = new StringBuilder();
                final Cursor goals = mDatabase.getColumnsForSelection(Goals.TABLE_NAME,
                        new String[]{Goals.COL_NAME_PLAYER_NAME, Goals.COL_NAME_PENALTY},
                        Goals.COL_NAME_FIXTURE_ID + "=?", new String[]{String.valueOf(mFixtureId)},
                        null);
                if (goals != null)
                {
                    if (goals.getCount() > 0)
                    {
                        do
                        {
                            builder.append(goals.getString(0));
                            if (goals.getInt(1) == 1)
                            {
                                builder.append(" (p)");
                            }
                            if (!goals.isLast())
                            {
                                builder.append(",\r\n");
                            }
                        } while (goals.moveToNext());
                        ((TextView)layout.findViewById(R.id.scorers)).setText(builder.toString());
                    }
                    else
                    {
                        layout.findViewById(R.id.scorers).setVisibility(View.GONE);
                    }
                    goals.close();
                }

                // Set the attendance
                DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.UK);
                DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
                symbols.setGroupingSeparator(',');
                formatter.setDecimalFormatSymbols(symbols);
                String attendance = formatter.format(mFixture.getInt(Fixtures.COL_ID_ATTENDANCE));
                TextView att = layout.findViewById(R.id.attendance);
                att.setText(String.format("Attendance: %s", attendance));
                att.setVisibility(View.VISIBLE);
            }

            mFixture.close();
        }

        return layout;
    }
}
