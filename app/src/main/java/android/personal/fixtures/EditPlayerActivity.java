package android.personal.fixtures;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.personal.fixtures.database.Database;
import android.personal.fixtures.database.tables.Players;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

public class EditPlayerActivity extends AppCompatActivity
{
    static final String EXTRA_PLAYER_ID = "player_id";

    private Database mDatabase;

    private EditText mForenameInput;
    private EditText mSurameInput;

    private boolean mIsEditMode;
    private long mPlayerId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_player);

        final Toolbar myToolbar = findViewById(R.id.edit_player_toolbar);
        setSupportActionBar(myToolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mDatabase = Database.getInstance(getApplicationContext());

        mForenameInput = findViewById(R.id.player_forename_input);
        mSurameInput = findViewById(R.id.player_surname_input);

        mIsEditMode = false;

        final Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            mIsEditMode = true;
            mPlayerId = extras.getLong(EXTRA_PLAYER_ID);
            final Cursor player = mDatabase.getFullRecordWithId(Players.TABLE_NAME, mPlayerId);
            if (player != null)
            {
                mForenameInput.setText(
                        player.getString(player.getColumnIndex(Players.COL_NAME_FORENAME)));
                mSurameInput.setText(
                        player.getString(player.getColumnIndex(Players.COL_NAME_SURNAME)));
                player.close();
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
                if (savePlayer())
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

    private boolean savePlayer()
    {
        if (TextUtils.isEmpty(mForenameInput.getText()))
        {
            return false;
        }
        if (TextUtils.isEmpty(mSurameInput.getText()))
        {
            return false;
        }

        final String forname = mForenameInput.getText().toString();
        final String surname = mSurameInput.getText().toString();

        final ContentValues values = new ContentValues();
        values.put(Players.COL_NAME_FORENAME, forname);
        values.put(Players.COL_NAME_SURNAME, surname);

        if (mIsEditMode)
        {
            return mDatabase.updateRecord(Players.TABLE_NAME, mPlayerId, values);
        }
        else
        {
            return mDatabase.addRecord(Players.TABLE_NAME, values) != -1;
        }
    }
}
