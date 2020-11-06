package android.personal.fixtures;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.personal.fixtures.database.Database;
import android.personal.fixtures.database.helpers.FixturesHelper;
import android.personal.fixtures.database.tables.Clubs;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Switch;

public class EditClubActivity extends AppCompatActivity
{
    static final String EXTRA_CLUB_ID = "club_id";

    private Database mDatabase;

    private EditText mFullNameInput;
    private EditText mShortNameInput;
    private EditText mCodeInput;
    private Switch mLeagueSwitch;

    private boolean mIsEditMode;
    private long mClubId;
    /** Store the original short name so we can update the fixtures if it changes. */
    private String mOldName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_club);

        final Toolbar myToolbar = findViewById(R.id.edit_club_toolbar);
        setSupportActionBar(myToolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mDatabase = Database.getInstance(getApplicationContext());

        mFullNameInput = findViewById(R.id.club_full_name_input);
        mShortNameInput = findViewById(R.id.club_short_name_input);
        mCodeInput = findViewById(R.id.club_code_input);
        mLeagueSwitch = findViewById(R.id.club_league_switch);

        mIsEditMode = false;
        mOldName = null;

        final Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            mIsEditMode = true;
            mClubId = extras.getLong(EXTRA_CLUB_ID);
            final Cursor club = mDatabase.getFullRecordWithId(Clubs.TABLE_NAME, mClubId);
            if (club != null)
            {
                mFullNameInput.setText(
                        club.getString(club.getColumnIndex(Clubs.COL_NAME_FULL_NAME)));
                final String name = club.getString(club.getColumnIndex(Clubs.COL_NAME_SHORT_NAME));
                mShortNameInput.setText(name);
                mOldName = name;
                mCodeInput.setText(club.getString(club.getColumnIndex(Clubs.COL_NAME_CODE)));
                mLeagueSwitch.setChecked(
                        club.getInt(club.getColumnIndex(Clubs.COL_NAME_IS_LEAGUE)) == 1);
                club.close();
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
                if (saveClub())
                {
                    // Check if we need to update any fixtures
                    final String name = mShortNameInput.getText().toString();
                    if (!name.equals(mOldName))
                    {
                        FixturesHelper.updateOppositionName(mDatabase, mOldName, name);
                    }

                    setResult(RESULT_OK);
                    finish();
                }
                break;

            case R.id.edit_action_delete:
                if (deleteClub())
                {
                    setResult(RESULT_OK);
                    finish();
                }
                break;

            case android.R.id.home:
                finish();
                break;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private boolean saveClub()
    {
        if (TextUtils.isEmpty(mFullNameInput.getText()))
        {
            return false;
        }
        if (TextUtils.isEmpty(mShortNameInput.getText()))
        {
            return false;
        }
        if (TextUtils.isEmpty(mCodeInput.getText()))
        {
            return false;
        }

        final String fullName = mFullNameInput.getText().toString();
        final String shortName = mShortNameInput.getText().toString();
        final String code = mCodeInput.getText().toString();

        final ContentValues values = new ContentValues();
        values.put(Clubs.COL_NAME_FULL_NAME, fullName);
        values.put(Clubs.COL_NAME_SHORT_NAME, shortName);
        values.put(Clubs.COL_NAME_CODE, code);
        values.put(Clubs.COL_NAME_IS_LEAGUE, mLeagueSwitch.isChecked() ? 1 : 0);

        if (mIsEditMode)
        {
            return mDatabase.updateRecord(Clubs.TABLE_NAME, mClubId, values);
        }
        else
        {
            return mDatabase.addRecord(Clubs.TABLE_NAME, values) != -1;
        }
    }

    private boolean deleteClub()
    {
        return mDatabase.deleteRecord(Clubs.TABLE_NAME, mClubId);
    }
}
