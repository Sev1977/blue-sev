package android.personal.fixtures;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.personal.fixtures.database.helpers.ClubsHelper;
import android.personal.fixtures.database.helpers.CompetitionsHelper;
import android.personal.fixtures.database.Database;
import android.personal.fixtures.database.StatsUpdate;
import android.personal.fixtures.database.tables.Clubs;
import android.personal.fixtures.database.tables.Fixtures;
import android.personal.fixtures.database.tables.Goals;
import android.personal.fixtures.database.tables.Seasons;
import android.personal.fixtures.fragments.DatePickerFragment;
import android.personal.fixtures.fragments.EditScoreFragment;
import android.personal.fixtures.fragments.TimePickerFragment;
import android.provider.BaseColumns;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class EditFixtureActivity extends AppCompatActivity
        implements EditScoreFragment.ScoreDialogListener
{
    static final String EXTRA_FIXTURE_ID = "fixture_id";

    private static final int REQUEST_COMPETITION = 0;
    private static final int REQUEST_OPPOSITION = 1;
    private static final int REQUEST_SCORE = 2;
    private static final int REQUEST_GOAL_SCORERS = 3;
    private static final int REQUEST_SEASON = 4;

    private static final String EXTRA_GOALS_SCORED = "goals_scored";
    private static final String EXTRA_GOALS_CONCEDED = "goals_conceded";
    static final String EXTRA_GOALS_SCORERS = "goal_scorers";

    private Database mDatabase;

    private Button mDateButton;
    private Button mTimeButton;
    private Button mCompetitionButton;
    private Button mOpponentButton;
    private RadioButton mHomeButton;
    private Button mScoreButton;
    private Button mGoalScorersButton;
    private EditText mAttendanceInput;
    private Button mSeasonButton;

    private boolean mIsEditMode;
    private long mFixtureId;

    private long mSeasonId;
    private int mDay;
    private int mMonth;
    private int mYear;
    private int mHours;
    private int mMinutes;

    private final View.OnClickListener mOnChoiceButtonClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View view)
        {
            Intent chooser = new Intent(EditFixtureActivity.this, ChooserListActivity.class);
            int requestId = 0;

            switch (view.getId())
            {
                case R.id.editFixtureCompetition:
                    chooser.putExtra(ChooserListActivity.EXTRA_ITEM_TYPE,
                            ChooserListActivity.ITEM_TYPE_COMPETITION);
                    requestId = REQUEST_COMPETITION;
                    break;

                case R.id.editFixtureOpponent:
                {
                    final CharSequence competitionName = mCompetitionButton.getText();
                    if (!TextUtils.isEmpty(competitionName) && CompetitionsHelper.getIsLeague(
                            mDatabase, competitionName.toString()))
                    {
                        chooser.putExtra(ChooserListActivity.EXTRA_FILTER,
                                Clubs.COL_NAME_IS_LEAGUE + "=1");
                    }
                    chooser.putExtra(ChooserListActivity.EXTRA_ITEM_TYPE,
                            ChooserListActivity.ITEM_TYPE_CLUB);
                    requestId = REQUEST_OPPOSITION;
                }
                break;

                case R.id.editFixtureSeason:
                    chooser.putExtra(ChooserListActivity.EXTRA_ITEM_TYPE,
                            ChooserListActivity.ITEM_TYPE_SEASON);
                    requestId = REQUEST_SEASON;
                    break;
            }

            startActivityForResult(chooser, requestId);
        }
    };

    private final View.OnClickListener mScoreButtonClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(final View view)
        {
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            final int[] score = TextUtils.isEmpty(mScoreButton.getText()) ? new int[]{0, 0} :
                    getScoreFromString(mScoreButton.getText().toString());
            final EditScoreFragment scoreDialog = EditScoreFragment.newInstance(score[0], score[1]);
            scoreDialog.setListener(EditFixtureActivity.this);
            scoreDialog.show(transaction, "score_dialog");
        }
    };

    private final View.OnClickListener mScorersButtonClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(final View view)
        {
            final Intent editGoals = new Intent(EditFixtureActivity.this,
                    EditGoalScorersActivity.class);
            editGoals.putExtra(EditGoalScorersActivity.EXTRA_FIXTURE_ID, mFixtureId);
            startActivityForResult(editGoals, REQUEST_GOAL_SCORERS);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_fixture);

        final Toolbar myToolbar = findViewById(R.id.edit_fixture_toolbar);
        setSupportActionBar(myToolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mDatabase = Database.getInstance(getApplicationContext());

        mDateButton = findViewById(R.id.editFixtureDate);
        mTimeButton = findViewById(R.id.editFixtureTime);

        mCompetitionButton = findViewById(R.id.editFixtureCompetition);
        mCompetitionButton.setOnClickListener(mOnChoiceButtonClicked);

        mOpponentButton = findViewById(R.id.editFixtureOpponent);
        mOpponentButton.setOnClickListener(mOnChoiceButtonClicked);

        mHomeButton = findViewById(R.id.editFixtureButtonHome);

        mScoreButton = findViewById(R.id.editFixtureScore);
        mScoreButton.setOnClickListener(mScoreButtonClickListener);

        mGoalScorersButton = findViewById(R.id.editFixtureScorers);
        mGoalScorersButton.setOnClickListener(mScorersButtonClickListener);

        mAttendanceInput = findViewById(R.id.editFixtureAttendance);

        mSeasonButton = findViewById(R.id.editFixtureSeason);
        mSeasonButton.setOnClickListener(mOnChoiceButtonClicked);

        mIsEditMode = false;

        // Set the default date and time
        final Calendar calendar = Calendar.getInstance();

        final Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            mIsEditMode = true;
            mFixtureId = extras.getLong(EXTRA_FIXTURE_ID);
            final Cursor fixture = mDatabase.getFullRecordWithId(Fixtures.TABLE_NAME, mFixtureId);
            if (fixture != null)
            {
                // Get the season name
                mSeasonId = fixture.getLong(Fixtures.COL_ID_SEASON_ID);
                updateSeasonButtonText();
                // Get the date
                final int unixTimestamp = fixture.getInt(Fixtures.COL_ID_DATE);
                calendar.setTimeInMillis(TimeUnit.SECONDS.toMillis(unixTimestamp));
                // Get the competition name
                mCompetitionButton.setText(fixture.getString(Fixtures.COL_ID_COMPETITION));
                // Get the opponent name
                mOpponentButton.setText(fixture.getString(Fixtures.COL_ID_OPPOSITION));
                // Get the venue
                final String venue = fixture.getString(Fixtures.COL_ID_VENUE);
                if (getString(R.string.home).equalsIgnoreCase(venue))
                {
                    mHomeButton.setChecked(true);
                }
                else
                {
                    ((RadioButton)findViewById(R.id.editFixtureButtonAway)).setChecked(true);
                }
                // Get the score
                updateScoreButtonText(fixture.getInt(Fixtures.COL_ID_GOALS_SCORED),
                        fixture.getInt(Fixtures.COL_ID_GOALS_CONCEDED));
                // Get the goal scorers
                mGoalScorersButton.setText(getGoalScorers());
                // Get the attendance
                mAttendanceInput.setText(
                        String.valueOf(fixture.getInt(Fixtures.COL_ID_ATTENDANCE)));

                fixture.close();
            }
        }

        mMinutes = calendar.get(Calendar.MINUTE);
        mHours = calendar.get(Calendar.HOUR_OF_DAY);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mMonth = calendar.get(Calendar.MONTH);
        mYear = calendar.get(Calendar.YEAR);
        updateDateButtonText();
        updateTimeButtonText();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu)
    {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_fixture, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.edit_fixture_action_delete:
                if (deleteFixture())
                {
                    setResult(RESULT_OK);
                    finish();
                }
                return true;

            case R.id.edit_fixture_action_save:
                if (saveFixture())
                {
                    // Start a new thread to update the statistics
                    new Thread(new StatsUpdate(getApplicationContext(), mSeasonId),
                            "StatsUpdateThread").start();
                    setResult(RESULT_OK);
                    finish();
                }
                return true;

            case android.R.id.home:
                finish();
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
        final long id = (resultCode == RESULT_OK) ? data.getLongExtra(
                ChooserListActivity.EXTRA_CHOSEN_ID, -1) : -1;
        switch (requestCode)
        {
            case REQUEST_COMPETITION:
                if (id != -1)
                {
                    mCompetitionButton.setText(
                            CompetitionsHelper.getShortNameFromId(mDatabase, id));
                }
                break;

            case REQUEST_OPPOSITION:
                if (id != -1)
                {
                    mOpponentButton.setText(ClubsHelper.getShortNameFromId(mDatabase, id));
                }
                break;

            case REQUEST_SEASON:
                if (id != -1)
                {
                    mSeasonId = id;
                    updateSeasonButtonText();
                }
                break;

            case REQUEST_SCORE:
                if (resultCode == RESULT_OK)
                {
                    final int scored = data.getIntExtra(EXTRA_GOALS_SCORED, 0);
                    final int conceded = data.getIntExtra(EXTRA_GOALS_CONCEDED, 0);
                    updateScoreButtonText(scored, conceded);
                }
                break;

            case REQUEST_GOAL_SCORERS:
                if (resultCode == RESULT_OK)
                {
                    mGoalScorersButton.setText(data.getStringExtra(EXTRA_GOALS_SCORERS));
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private boolean saveFixture()
    {
        if (TextUtils.isEmpty(mOpponentButton.getText()) || (mSeasonId == -1))
        {
            return false;
        }

        final Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(mYear, mMonth, mDay, mHours, mMinutes);
        final long dateTime = calendar.getTimeInMillis();
        final String venue = mHomeButton.isChecked() ? getString(R.string.home) : getString(
                R.string.away);
        final CharSequence score = mScoreButton.getText();
        final CharSequence attendance = mAttendanceInput.getText();

        final ContentValues values = new ContentValues();
        values.put(Fixtures.COL_NAME_DATE, TimeUnit.MILLISECONDS.toSeconds(dateTime));
        values.put(Fixtures.COL_NAME_COMPETITION, mCompetitionButton.getText().toString());
        values.put(Fixtures.COL_NAME_VENUE, venue);
        values.put(Fixtures.COL_NAME_OPPOSITION, mOpponentButton.getText().toString());
        /*
         * Goals scored and conceded
         */
        if (!TextUtils.isEmpty(score))
        {
            final int[] goals = getScoreFromString(score.toString());
            values.put(Fixtures.COL_NAME_GOALS_SCORED, goals[0]);
            values.put(Fixtures.COL_NAME_GOALS_CONCEDED, goals[1]);
        }
        if (!TextUtils.isEmpty(attendance))
        {
            values.put(Fixtures.COL_NAME_ATTENDANCE, attendance.toString());
        }
        values.put(Fixtures.COL_NAME_SEASON_ID, mSeasonId);

        if (mIsEditMode)
        {
            return mDatabase.updateRecord(Fixtures.TABLE_NAME, mFixtureId, values);
        }
        else
        {
            return mDatabase.addRecord(Fixtures.TABLE_NAME, values) != -1;
        }
    }

    private boolean deleteFixture()
    {
        // If the date is in the future, we can safely delete the fixture without any knock on
        // effect, otherwise, if the fixture is in the past, we'd have to recalculate all the
        // statistics again.  Therefore, we won't allow the user to delete a fixture from teh last.

        final Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(mYear, mMonth, mDay, mHours, mMinutes);
        final long dateTime = calendar.getTimeInMillis();
        if (dateTime <= System.currentTimeMillis())
        {
            return false;
        }

        return mDatabase.deleteRecord(Fixtures.TABLE_NAME, mFixtureId);
    }

    /**
     * Get the goals scored and conceded from the given String.
     *
     * @param score String representation of the score in the format, "scored - conceded".
     * @return Array of integers for the goals scored (#0) and conceded (#1).
     */
    private int[] getScoreFromString(final String score)
    {
        if (score != null)
        {
            final int[] scores = new int[2];
            scores[0] = Integer.valueOf(score.substring(0, score.indexOf(" ")));
            scores[1] = Integer.valueOf(score.substring(score.lastIndexOf(" ") + 1));
            return scores;
        }

        return null;
    }

    private final DatePickerFragment.DateSelectedListener mDateSelectedListener =
            new DatePickerFragment.DateSelectedListener()
            {
                @Override
                public void onDateSelected(final int year, final int month, final int day)
                {
                    mDay = day;
                    mMonth = month;
                    mYear = year;
                    updateDateButtonText();
                }
            };

    public void showDatePicker(View view)
    {
        final DatePickerFragment fragment = new DatePickerFragment();
        final Calendar calendar = Calendar.getInstance();
        calendar.set(mYear, mMonth, mDay);
        fragment.setDate(calendar.getTimeInMillis());
        fragment.setListener(mDateSelectedListener);
        fragment.show(getFragmentManager(), "datePicker");
    }

    private final TimePickerFragment.TimeSelectedListener mTimeSelectedListener =
            new TimePickerFragment.TimeSelectedListener()
            {
                @Override
                public void onTimeSelected(final int hourOfDay, final int minute)
                {
                    mHours = hourOfDay;
                    mMinutes = minute;
                    updateTimeButtonText();
                }
            };

    public void showTimePicker(View view)
    {
        final TimePickerFragment fragment = new TimePickerFragment();
        final Calendar calendar = Calendar.getInstance();
        calendar.set(mYear, mMonth, mDay, mHours, mMinutes);
        fragment.setTime(calendar.getTimeInMillis());
        fragment.setListener(mTimeSelectedListener);
        fragment.show(getFragmentManager(), "timePicker");
    }

    private void updateDateButtonText()
    {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(mYear, mMonth, mDay, mHours, mMinutes);
        mDateButton.setText(new SimpleDateFormat(getString(R.string.short_date_format), Locale.UK)
                .format(calendar.getTime()));
    }

    private void updateTimeButtonText()
    {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(mYear, mMonth, mDay, mHours, mMinutes);
        mTimeButton.setText(new SimpleDateFormat(getString(R.string.time_format), Locale.UK)
                .format(calendar.getTime()));
    }

    private void updateSeasonButtonText()
    {
        final Cursor season = mDatabase.getColumnsForSelection(Seasons.TABLE_NAME,
                new String[]{Seasons.COL_NAME_NAME}, BaseColumns._ID + "=?",
                new String[]{String.valueOf(mSeasonId)}, Seasons.DEFAULT_SORT_ORDER);
        mSeasonButton.setText(season.getString(0));
    }

    private void updateScoreButtonText(final int scored, final int conceded)
    {
        mScoreButton.setText(getString(R.string.score_format, scored, conceded));
    }

    private String getGoalScorers()
    {
        final Cursor goals = mDatabase.getColumnsForSelection(Goals.TABLE_NAME,
                new String[]{Goals.COL_NAME_PLAYER_NAME, Goals.COL_NAME_PENALTY},
                Goals.COL_NAME_FIXTURE_ID + "=?", new String[]{String.valueOf(mFixtureId)}, null);
        if (goals != null)
        {
            final StringBuilder scorers = new StringBuilder();
            if (goals.getCount() > 0)
            {
                do
                {
                    scorers.append(goals.getString(0));
                    if (goals.getInt(1) == 1)
                    {
                        scorers.append(" (P)");
                    }
                    if (!goals.isLast())
                    {
                        scorers.append(", ");
                    }
                } while (goals.moveToNext());
            }

            goals.close();

            return scorers.toString();
        }

        return null;
    }

    @Override
    public void onOkClicked(final int[] score)
    {
        updateScoreButtonText(score[0], score[1]);
        getSupportFragmentManager().beginTransaction().remove(
                getSupportFragmentManager().findFragmentByTag("score_dialog")).commit();
    }
}
