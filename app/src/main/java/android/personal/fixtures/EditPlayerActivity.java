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
import android.widget.Switch;

public class EditPlayerActivity extends AppCompatActivity
{
    static final String EXTRA_PLAYER_ID = "player_id";

    private Database mDatabase;

    private EditText mForenameInput;
    private EditText mSurnameInput;
    private Switch mCurrentSwitch;

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
        mSurnameInput = findViewById(R.id.player_surname_input);
        mCurrentSwitch = findViewById((R.id.player_current_switch));

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
                mSurnameInput.setText(
                        player.getString(player.getColumnIndex(Players.COL_NAME_SURNAME)));
                mCurrentSwitch.setChecked(
                        1 == player.getInt(player.getColumnIndex(Players.COL_NAME_CURRENT)));
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
            case R.id.edit_action_delete:
                if (deletePlayer())
                {
                    setResult(RESULT_OK);
                    finish();
                }
                break;

            case R.id.edit_action_save:
                if (savePlayer())
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

    private boolean savePlayer()
    {
        if (TextUtils.isEmpty(mForenameInput.getText()))
        {
            return false;
        }
        if (TextUtils.isEmpty(mSurnameInput.getText()))
        {
            return false;
        }

        final String forename = mForenameInput.getText().toString();
        final String surname = mSurnameInput.getText().toString();
        final boolean isCurrent = mCurrentSwitch.isChecked();

        final ContentValues values = new ContentValues();
        values.put(Players.COL_NAME_FORENAME, forename);
        values.put(Players.COL_NAME_SURNAME, surname);
        values.put(Players.COL_NAME_CURRENT, isCurrent ? 1 : 0);

        if (mIsEditMode)
        {
            return mDatabase.updateRecord(Players.TABLE_NAME, mPlayerId, values);
        }
        else
        {
            return mDatabase.addRecord(Players.TABLE_NAME, values) != -1;
        }
    }

    private boolean deletePlayer()
    {
        return mDatabase.deleteRecord(Players.TABLE_NAME, mPlayerId);
    }
}
