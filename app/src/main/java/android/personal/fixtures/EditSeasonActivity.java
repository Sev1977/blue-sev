package android.personal.fixtures;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.personal.fixtures.database.Database;
import android.personal.fixtures.database.tables.Seasons;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

public class EditSeasonActivity extends AppCompatActivity
{
    static final String EXTRA_SEASON_ID = "season_id";

    private Database mDatabase;

    private EditText mNameInput;

    private boolean mIsEditMode;
    private long mSeasonId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_season);

        final Toolbar myToolbar = findViewById(R.id.edit_season_toolbar);
        setSupportActionBar(myToolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mDatabase = Database.getInstance(getApplicationContext());

        mNameInput = findViewById(R.id.season_name_input);

        mIsEditMode = false;

        final Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            mIsEditMode = true;
            mSeasonId = extras.getLong(EXTRA_SEASON_ID);
            final Cursor season = mDatabase.getFullRecordWithId(Seasons.TABLE_NAME, mSeasonId);
            if (season != null)
            {
                mNameInput.setText(season.getString(Seasons.COL_ID_NAME));
                season.close();
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
                if (saveSeason())
                {
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

    private boolean saveSeason()
    {
        if (TextUtils.isEmpty(mNameInput.getText()))
        {
            return false;
        }

        final ContentValues values = new ContentValues();
        values.put(Seasons.COL_NAME_NAME, mNameInput.getText().toString());

        if (mIsEditMode)
        {
            return mDatabase.updateRecord(Seasons.TABLE_NAME, mSeasonId, values);
        }
        else
        {
            return mDatabase.addRecord(Seasons.TABLE_NAME, values) != -1;
        }
    }
}
