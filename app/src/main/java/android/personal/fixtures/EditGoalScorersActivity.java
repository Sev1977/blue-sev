package android.personal.fixtures;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.personal.fixtures.database.Database;
import android.personal.fixtures.database.helpers.PlayersHelper;
import android.personal.fixtures.database.tables.Goals;
import android.personal.fixtures.database.tables.Players;
import android.provider.BaseColumns;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class EditGoalScorersActivity extends AppCompatActivity
{
    private static final int REQUEST_PLAYER = 0;

    static final String EXTRA_FIXTURE_ID = "fixture_id";

    private static final int NAME_VIEW_ID = R.id.goalScorerName;
    private static final int PENALTY_VIEW_ID = R.id.goalScorerPenalty;

    private LinearLayout mList;
    private TextView mSelectedNameView;

    private Database mDatabase;
    private long mFixtureId;

    private ArrayList<Integer> mPlayerIds;

    private final View.OnClickListener mNameClickListener = this::OnNameClicked;
    private final View.OnClickListener mDeleteGoalListener = new View.OnClickListener()
    {
        @Override
        public void onClick(final View view)
        {
            final LinearLayout listItem = (LinearLayout)view.getParent();
            mDatabase.getWritableDatabase().delete(Goals.TABLE_NAME, BaseColumns._ID + "=?",
                    new String[]{String.valueOf(listItem.getTag())});
            mList.removeView(listItem);
        }
    };

    protected void OnNameClicked(final View view)
    {
        mSelectedNameView = (TextView)view;
        final Intent chooser = new Intent(EditGoalScorersActivity.this,
                ChooserListActivity.class);
        chooser.putExtra(ChooserListActivity.EXTRA_ITEM_TYPE,
                ChooserListActivity.ITEM_TYPE_PLAYER);
        startActivityForResult(chooser, REQUEST_PLAYER);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_goal_scorers);

        final Toolbar myToolbar = findViewById(R.id.scorersTitle);
        setSupportActionBar(myToolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mList = findViewById(R.id.scorersList);

        mDatabase = Database.getInstance(EditGoalScorersActivity.this);

        mFixtureId = getIntent().getLongExtra(EXTRA_FIXTURE_ID, -1);

        mPlayerIds = new ArrayList<>(0);

        // Check if there are any existing goals for this mCurrentFixture and add them to the list.
        final Cursor goals = mDatabase.getSelection(Goals.TABLE_NAME,
                Goals.COL_NAME_FIXTURE_ID + "=?", new String[]{String.valueOf(mFixtureId)}, null);
        if (goals != null)
        {
            if (goals.moveToFirst())
            {
                do
                {
                    addAnotherScorer(null);
                    final View item = mList.getChildAt(mList.getChildCount() - 1);
                    item.setTag(goals.getLong(0)); // set the goal ID as the tag for this view

                    // Get the player record
                    final Cursor player = mDatabase.getFullRecordWithId(Players.TABLE_NAME,
                            goals.getInt(Goals.COL_ID_PLAYER_ID));
                    if (player != null && player.moveToFirst()) {
                        mPlayerIds.add(player.getInt(0));
                        // Get the name
                        final String first = player.getString(Players.COL_ID_FORENAME);
                        final String second = player.getString(Players.COL_ID_SURNAME);
                        ((TextView) item.findViewById(NAME_VIEW_ID))
                                .setText(String.format("%s %s", first, second));
                    }
                    // Get the penalty 'flag'
                    ((CheckBox)item.findViewById(PENALTY_VIEW_ID))
                            .setChecked(goals.getInt(Goals.COL_ID_PENALTY) == 1);
                } while (goals.moveToNext());
            }

            goals.close();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        if (requestCode == REQUEST_PLAYER)
        {
            if (resultCode == RESULT_OK)
            {
                final long playerId = data.getLongExtra(ChooserListActivity.EXTRA_CHOSEN_ID, -1);
                mSelectedNameView.setText(PlayersHelper.getFullNameFromId(mDatabase, playerId));
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
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
            {
                final String scorers = saveGoalScorers();
                if (scorers != null)
                {
                    final Intent intent = new Intent().putExtra(
                            EditFixtureActivity.EXTRA_GOALS_SCORERS, scorers);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                return true;
            }

            case android.R.id.home:
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    public void addAnotherScorer(View view)
    {
        // Create a new scorer list item and add it to the list.
        final LinearLayout layout = (LinearLayout)getLayoutInflater().inflate(
                R.layout.goal_scorers_list_item, null, false);
        mList.addView(layout);
        View nameView = layout.findViewById(NAME_VIEW_ID);
        nameView.setOnClickListener(mNameClickListener);
        layout.findViewById(R.id.goalDeleteButton).setOnClickListener(mDeleteGoalListener);

        if (view != null)
            OnNameClicked(nameView);
    }

    public String saveGoalScorers()
    {
        if (mFixtureId != -1)
        {
            // First, to make my life easier, I'm going to delete all existing goals for this
            // mCurrentFixture.
            mDatabase.getWritableDatabase().delete(Goals.TABLE_NAME,
                    Goals.COL_NAME_FIXTURE_ID + "=?", new String[]{String.valueOf(mFixtureId)});

            // Now I'll add a new record for each goal.
            final StringBuilder scorers = new StringBuilder();
            // Traverse the list and save a goal in the DB for each item.
            final int count = mList.getChildCount();
            for (int i = 0; i < count; i++)
            {
                final View listItem = mList.getChildAt(i);
                final TextView nameView = listItem.findViewById(NAME_VIEW_ID);
                if (!TextUtils.isEmpty(nameView.getText()))
                {
                    final String playerName = nameView.getText().toString();
                    final int penalty = ((CheckBox)listItem.findViewById(PENALTY_VIEW_ID))
                            .isChecked() ? 1 : 0;

                    final ContentValues values = new ContentValues();
                    values.put(Goals.COL_NAME_FIXTURE_ID, mFixtureId);
                    // Split the player name and search the players table for the ID
                    final String[] names = playerName.split(" ");
                    final Cursor player = PlayersHelper.getPlayerIdFromNames(mDatabase, names[0], names[1]);
                    if ((player == null) || (player.getCount() == 0))
                        return null;

                    values.put(Goals.COL_NAME_PLAYER_ID, player.getInt(0));
                    values.put(Goals.COL_NAME_PENALTY, penalty);
                    mDatabase.addRecord(Goals.TABLE_NAME, values);

                    scorers.append(playerName);
                    if (penalty == 1)
                    {
                        scorers.append(" (P)");
                    }
                    if (i < (count - 1))
                    {
                        scorers.append(", ");
                    }
                }
            }

            return scorers.toString();
        }

        return null;
    }
}
