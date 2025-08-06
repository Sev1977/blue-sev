package android.personal.fixtures;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.personal.fixtures.database.Database;
import android.personal.fixtures.database.helpers.ClubsHelper;
import android.personal.fixtures.database.helpers.PlayersHelper;
import android.personal.fixtures.database.tables.Fixtures;
import android.personal.fixtures.database.tables.Goals;
import android.provider.BaseColumns;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class FixtureActivity extends AppCompatActivity
{
    public static final String EXTRA_FIXTURE_ID = "fixture_id";
    public static final String EXTRA_FIXTURE_EDITED = "edited";

    public static final int REQUEST_EDIT = 0;

    private long mFixtureId;
    private boolean mIsEdited;
    private Database mDatabase;
    private Cursor mFixtures;
    private Cursor mCurrentFixture;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixture);

        mIsEdited = false;
        mFixtureId = -1;

        setSupportActionBar((Toolbar)findViewById(R.id.my_toolbar));
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mDatabase = Database.getInstance(getApplicationContext());

        mFixtures = mDatabase.getAllRecords(Fixtures.TABLE_NAME, Fixtures.DEFAULT_SORT_ORDER);

        final Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            mFixtureId = extras.getLong(EXTRA_FIXTURE_ID, -1);
            if (mFixtureId != -1)
            {
                mCurrentFixture = mDatabase.getFullRecordWithId(Fixtures.TABLE_NAME, mFixtureId);

                // Move the Cursor position to the current record.
                final int colIndex = mFixtures.getColumnIndex(BaseColumns._ID);
                while (mFixtures.getInt(colIndex) != mFixtureId)
                {
                    mFixtures.moveToNext();
                }

                setupViews();
            }
        }

        // Set up the 'previous' button
        findViewById(R.id.btnPreviousFixture).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View view)
            {
                if (!mFixtures.isFirst())
                {
                    mFixtures.moveToPrevious();
                    loadNewFixture();
                }
            }
        });
        // Set up the 'next' button
        findViewById(R.id.btnNextFixture).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View view)
            {
                if (!mFixtures.isLast())
                {
                    mFixtures.moveToNext();
                    loadNewFixture();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu)
    {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_fixture, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.fixture_action_edit:
                final Intent edit = new Intent(FixtureActivity.this, EditFixtureActivity.class);
                edit.putExtra(EditFixtureActivity.EXTRA_FIXTURE_ID, mFixtureId);
                startActivityForResult(edit, REQUEST_EDIT);
                return true;

            case android.R.id.home:
                leave();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        if (requestCode == REQUEST_EDIT)
        {
            if (resultCode == RESULT_OK)
            {
                mIsEdited = true;
                loadNewFixture();
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed()
    {
        leave();
    }

    private void setupViews()
    {
        if (mCurrentFixture != null)
        {
            // Set the date
            final int unixTimestamp = mCurrentFixture.getInt(Fixtures.COL_ID_DATE);
            final Calendar calendar = Calendar.getInstance();
            final long now = calendar.getTimeInMillis(); // store the time now for later use.
            calendar.setTimeInMillis(TimeUnit.SECONDS.toMillis(unixTimestamp));
            ((TextView)findViewById(R.id.dateTime)).setText(
                    new SimpleDateFormat(getString(R.string.long_date_format), Locale.UK)
                            .format(calendar.getTime()));

            // Set the opposition
            final String shortName = mCurrentFixture.getString(Fixtures.COL_ID_OPPOSITION);
            final String fullName = ClubsHelper.getFullNameFromShortName(mDatabase, shortName);
            ((TextView)findViewById(R.id.opposition)).setText(fullName);

            // Set the venue
            ((TextView)findViewById(R.id.homeOrAway)).setText(
                    mCurrentFixture.getString(Fixtures.COL_ID_VENUE));

            // Set the competition
            ((TextView)findViewById(R.id.competition)).setText(
                    mCurrentFixture.getString(Fixtures.COL_ID_COMPETITION));

            // Show the score, scorers and attendance.  If it's a fixture, the views will be empty.
            String score = "";
            final Resources resources = getResources();
            int scoreColour = resources.getColor(R.color.result_draw);
            String scorers = "";
            String attendance = "";
            if (calendar.getTimeInMillis() < now)
            {
                // Set the score
                final int scored = mCurrentFixture.getInt(Fixtures.COL_ID_GOALS_SCORED);
                final int conceded = mCurrentFixture.getInt(Fixtures.COL_ID_GOALS_CONCEDED);
                score = getString(R.string.score_format, scored, conceded);
                if (scored > conceded)
                {
                    scoreColour = resources.getColor(R.color.result_win);
                }
                else if (scored < conceded)
                {
                    scoreColour = resources.getColor(R.color.result_defeat);
                }

                // Set the goal scorers
                final StringBuilder builder = new StringBuilder();
                final Cursor goals = mDatabase.getColumnsForSelection(Goals.TABLE_NAME,
                        new String[]{Goals.COL_NAME_PLAYER_ID, Goals.COL_NAME_PENALTY},
                        Goals.COL_NAME_FIXTURE_ID + "=?", new String[]{String.valueOf(mFixtureId)},
                        null);
                if (goals != null)
                {
                    if (goals.getCount() > 0)
                    {
                        do
                        {
                            String playerName =
                                    PlayersHelper.getFullNameFromId(mDatabase, goals.getLong(0));
                            builder.append(playerName);
                            if (goals.getInt(1) == 1)
                            {
                                builder.append(" (p)");
                            }
                            if (!goals.isLast())
                            {
                                builder.append(",\r\n");
                            }
                        } while (goals.moveToNext());
                    }
                    goals.close();
                }
                scorers = builder.toString();

                // Set the attendance
                DecimalFormat formatter = (DecimalFormat)NumberFormat.getInstance(Locale.UK);
                DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
                symbols.setGroupingSeparator(',');
                formatter.setDecimalFormatSymbols(symbols);
                attendance = formatter.format(mCurrentFixture.getInt(Fixtures.COL_ID_ATTENDANCE));
            }
            final TextView scoreView = findViewById(R.id.score);
            scoreView.setText(score);
            scoreView.setTextColor(scoreColour);
            ((TextView)findViewById(R.id.scorers)).setText(scorers);
            ((TextView)findViewById(R.id.attendance)).setText(attendance);

            mCurrentFixture.close();
        }
    }

    private void leave()
    {
        setResult(RESULT_OK, new Intent().putExtra(EXTRA_FIXTURE_EDITED, mIsEdited));
        finish();
    }

    private void loadNewFixture()
    {
        mFixtureId = mFixtures.getLong(0);
        mCurrentFixture = mDatabase.getFullRecordWithId(Fixtures.TABLE_NAME, mFixtureId);
        setupViews();
    }
}
