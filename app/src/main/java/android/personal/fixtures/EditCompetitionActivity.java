package android.personal.fixtures;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.personal.fixtures.database.Database;
import android.personal.fixtures.database.helpers.FixturesHelper;
import android.personal.fixtures.database.tables.Competitions;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Switch;

public class EditCompetitionActivity extends AppCompatActivity
{
    static final String EXTRA_COMPETITION_ID = "competition_id";

    private Database mDatabase;

    private EditText mFullNameInput;
    private EditText mShortNameInput;
    private Switch mCompIsLeague;

    private boolean mIsEditMode;
    private long mCompetitionId;
    /** Store the original name so we can update the fixtures if it changes. */
    private String mOldName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_competition);

        final Toolbar myToolbar = findViewById(R.id.edit_competition_toolbar);
        setSupportActionBar(myToolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mDatabase = Database.getInstance(getApplicationContext());

        mFullNameInput = findViewById(R.id.competition_full_name_input);
        mShortNameInput = findViewById(R.id.competition_short_name_input);
        mCompIsLeague = findViewById(R.id.compIsLeagueSwitch);

        mIsEditMode = false;
        mOldName = null;

        final Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            mIsEditMode = true;
            mCompetitionId = extras.getLong(EXTRA_COMPETITION_ID);
            final Cursor competition = mDatabase.getFullRecordWithId(Competitions.TABLE_NAME,
                    mCompetitionId);
            if (competition != null)
            {
                mFullNameInput.setText(competition.getString(Competitions.COL_INDEX_FULL_NAME));
                final String shortName = competition.getString(Competitions.COL_INDEX_SHORT_NAME);
                mShortNameInput.setText(shortName);
                mOldName = shortName;
                mCompIsLeague.setChecked(competition.getInt(Competitions.COL_INDEX_IS_LEAGUE) == 1);
                competition.close();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu)
    {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.edit_action_save:
                if (saveCompetition())
                {
                    // Check if we need to update any fixtures
                    final String name = mShortNameInput.getText().toString();
                    if (!name.equals(mOldName))
                    {
                        FixturesHelper.updateCompetitionName(mDatabase, mOldName, name);
                    }

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

    private boolean saveCompetition()
    {
        if (TextUtils.isEmpty(mFullNameInput.getText()))
        {
            return false;
        }
        if (TextUtils.isEmpty(mShortNameInput.getText()))
        {
            return false;
        }

        final String fullName = mFullNameInput.getText().toString();
        final String shortName = mShortNameInput.getText().toString();

        final ContentValues values = new ContentValues();
        values.put(Competitions.COL_NAME_FULL_NAME, fullName);
        values.put(Competitions.COL_NAME_SHORT_NAME, shortName);
        values.put(Competitions.COL_NAME_IS_LEAGUE, mCompIsLeague.isChecked());

        if (mIsEditMode)
        {
            return mDatabase.updateRecord(Competitions.TABLE_NAME, mCompetitionId, values);
        }
        else
        {
            return mDatabase.addRecord(Competitions.TABLE_NAME, values) != -1;
        }
    }
}
